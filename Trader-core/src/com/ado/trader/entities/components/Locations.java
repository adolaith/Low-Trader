package com.ado.trader.entities.components;

import com.ado.trader.buildings.Building;
import com.ado.trader.buildings.WorkArea;
import com.artemis.Component;

public class Locations extends Component {
	Building home;
	WorkArea work;

	public Locations() {
		home = null;
		work = null;
	}
	public Building getHome() {
		return home;
	}
	public WorkArea getWork() {
		return work;
	}
	public void setHome(Building home) {
		this.home = home;
	}
	public void setWork(WorkArea work) {
		this.work = work;
	}
}
