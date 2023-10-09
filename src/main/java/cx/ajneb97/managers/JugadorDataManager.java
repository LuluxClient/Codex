package main.java.cx.ajneb97.managers;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import main.java.cx.ajneb97.Codex;
import main.java.cx.ajneb97.data.AgregarEntradaCallback;
import main.java.cx.ajneb97.data.JugadorCodex;
import main.java.cx.ajneb97.data.JugadorCodexCallback;
import main.java.cx.ajneb97.data.MySQL;

import java.util.ArrayList;
import java.util.List;

public class JugadorDataManager {

	private List<JugadorCodex> jugadores;
	private Codex plugin;

	public JugadorDataManager(Codex plugin) {
		this.jugadores = new ArrayList<>();
		this.plugin = plugin;
	}
	public List<JugadorCodex> getJugadores() {
		return jugadores;
	}
	public void setJugadores(ArrayList<JugadorCodex> jugadores) {
		this.jugadores = jugadores;
	}
	public void agregarJugador(JugadorCodex j) {
		jugadores.add(j);
	}

	public JugadorCodex getJugadorSync(String uuid) {
		JugadorCodex jugadorCodex = null;
		if (MySQL.isEnabled(plugin.getConfig())) {
			jugadorCodex = MySQL.getJugador(uuid, plugin);
		} else {
			for (JugadorCodex j : jugadores) {
				if (j.getUuid().equals(uuid)) {
					jugadorCodex = j;
					break;
				}
			}
		}
		return jugadorCodex;
	}

	public void getJugador(final String uuid, final JugadorCodexCallback callback) {
		new BukkitRunnable() {
			@Override
			public void run() {
				final JugadorCodex jugadorCodex = getJugadorSync(uuid);

				new BukkitRunnable() {
					@Override
					public void run() {
						callback.onDone(jugadorCodex);
					}
				}.runTask(plugin);
			}
		}.runTaskAsynchronously(plugin);
	}

	public void agregarEntrada(final Player jugador, final String categoria, final String discovery,
							   final AgregarEntradaCallback callback) {
		getJugador(jugador.getUniqueId().toString(), new JugadorCodexCallback() {
			@Override
			public void onDone(JugadorCodex j) {
				boolean agrega = false;
				if (MySQL.isEnabled(plugin.getConfig())) {
					if (j == null) {
						j = new JugadorCodex(jugador.getUniqueId().toString(), jugador.getName());
						j.agregarEntrada(categoria, discovery);
						agrega = true;
						MySQL.actualizarDiscoveriesJugador(plugin, j);
					} else {
						agrega = j.agregarEntrada(categoria, discovery);
						if (agrega) {
							MySQL.actualizarDiscoveriesJugador(plugin, j);
						}
					}
				} else {
					if (j == null) {
						j = new JugadorCodex(jugador.getUniqueId().toString(), jugador.getName());
						agregarJugador(j);
					}

					agrega = j.agregarEntrada(categoria, discovery);
				}

				final boolean agregaFinal = agrega;

				new BukkitRunnable() {
					@Override
					public void run() {
						callback.onDone(agregaFinal);
					}
				}.runTask(plugin);
			}
		});
	}

	public void resetearEntrada(JugadorCodex j, String categoria, String discovery, boolean todas) {
		if (MySQL.isEnabled(plugin.getConfig())) {
			if (todas) {
				j.resetearEntradas();
			} else {
				j.resetearEntrada(categoria, discovery);
			}
			MySQL.actualizarDiscoveriesJugador(plugin, j);
		} else {
			if (todas) {
				j.resetearEntradas();
			} else {
				j.resetearEntrada(categoria, discovery);
			}

		}
	}
}
