package com.ado.trader.map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class WorkZone extends Zone {
	public Array<WorkArea> workAreas;

	public WorkZone(int id, Vector2 zone, ZoneType type) {
		super(id, zone, type);
		workAreas = new Array<WorkZone.WorkArea>();
	}

	public WorkZone(int id, Array<Vector2> area, ZoneType type) {
		super(id, area, type);
		workAreas = new Array<WorkZone.WorkArea>();
	}
	
	public void addWorkTile(Vector2 vec, String aiProfile){
		workAreas.add(new WorkArea(vec, aiProfile));
	}
	
	public WorkArea getWorkArea(Vector2 vec){
		for(WorkArea a: workAreas){
			if(a.vec != null){
				if(a.vec.x == vec.x && a.vec.y == vec.y){
					return a;
				}
			}
			if(a.area != null){
				for(Vector2 t: a.area){
					if(t.x == vec.x && t.y == vec.y){
						return a;
					}
				}
			}
		}
		return null;
	}
	
	public void removeWorkTile(Vector2 click){
		WorkArea a = getWorkArea(click);
		if(a != null){
			workAreas.removeValue(a, false);
		}
	}
	
	public void addWorkArea(Array<Vector2> area, String aiProfile){
		workAreas.add(new WorkArea(area, aiProfile));
	}
	
	public void updateWorkArea(Array<Vector2> area){
		WorkArea a = getWorkArea(area.first());
		a.area.clear();
		a.area.addAll(area);
	}
	
	public WorkArea findWork(int id){
		for(WorkArea a: workAreas){
			if(a.vec == null && a.entityId == null){
				a.entityId = id;
				return a;
			}
			if(a.area != null){
				if(a.allEntities.size < a.area.size / 4){
					a.allEntities.add(id);
					return a;
				}
			}
			
		}
		return null;
	}
	
	public boolean isWorkTile(int x, int y){
		for(WorkArea a: workAreas){
			if(a.vec != null){
				if(a.vec.x == x && a.vec.y == y){
					return true;
				}
			}
			if(a.area != null){
				for(Vector2 vec: a.area){
					if(vec.x == x && vec.y == y){
						return true;
					}	
				}
			}
		}
		
		return false;
	}
	
	public void removeWorker(int id){
		for(WorkArea a: workAreas){
			if(a.vec != null){
				if(a.entityId == id){
					a.entityId = null;
					return;
				}
			}
			if(a.area != null){
				a.allEntities.removeValue(id, true);
			}
		}
	}

	public class WorkArea{
		public Vector2 vec;
		public Array<Vector2> area;
		public Integer entityId;
		public Array<Integer> allEntities;
		public String aiWorkProfile;
		
		public WorkArea(Vector2 vec, String aiProfile){
			this.vec = vec;
			this.entityId = null;
			this.aiWorkProfile = aiProfile;
		}
		public WorkArea(Array<Vector2> area, String aiProfile){
			this.area = area;
			this.aiWorkProfile = aiProfile;
			this.allEntities = new Array<Integer>();
		}
	}
}
