package com.ado.trader.entities.components;

import com.ado.trader.systems.StatusIconSystem.StatusIcon;
import com.artemis.Component;

public class Status extends Component {
	StatusIcon icon;  

	public Status(StatusIcon icon) {
		this.icon = icon;
	}
	public StatusIcon getStatusIcon(){
		return icon;
	}
}
