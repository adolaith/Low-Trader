package com.ado.trader.entities.components;

import com.artemis.Component;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/*A non-pooled save/load-able component. Must be extended
 * 
 */
public abstract class SerializableComponent extends Component {

	public abstract void save(Json writer);
	public abstract void load(JsonValue data);
	
}
