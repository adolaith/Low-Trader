package com.ado.trader.entities.components;

import com.ado.trader.map.Zone;
import com.artemis.Component;

public class Locations extends Component {
	Zone home, work;

	public Locations() {
		home = null;
		work = null;
	}
	public Zone getHome() {
		return home;
	}
	public Zone getWork() {
		return work;
	}
	public void setHome(Zone home) {
		this.home = home;
	}
	public void setWork(Zone work) {
		this.work = work;
	}
}
