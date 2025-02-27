package io.anuke.moment;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import io.anuke.moment.ai.Pathfind;
import io.anuke.moment.entities.*;
import io.anuke.moment.resource.Item;
import io.anuke.moment.resource.ItemStack;
import io.anuke.moment.resource.Recipe;
import io.anuke.moment.world.Generator;
import io.anuke.moment.world.Tile;
import io.anuke.moment.world.TileType;
import io.anuke.ucore.core.KeyBinds;
import io.anuke.ucore.core.Settings;
import io.anuke.ucore.core.USound;
import io.anuke.ucore.entities.Effects;
import io.anuke.ucore.entities.Entities;
import io.anuke.ucore.modules.ModuleController;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public class Moment extends ModuleController<Moment>{
	public static Moment i;
	public Player player;
	public int size = 128;
	public int pixsize = size*TileType.tilesize;
	public Tile[][] tiles = new Tile[size][size];
	public ObjectMap<Item, Integer> items = new ObjectMap<>();
	public Recipe recipe;
	public int rotation;
	public float placerange = 60;
	public float respawntime = 60*5;
	
	public int wave = 1;
	public float wavespace = 35*60;
	public float wavetime;
	public float spawnspace = 65;
	public Tile core;
	public Array<Tile> spawnpoints = new Array<Tile>();
	
	public boolean playing = false;
	public boolean paused = false;
	public boolean showedTutorial = false;

	@Override
	public void init(){
		i = this;
		
		addModule(new Control());
		addModule(new UI());
	}
	
	@Override
	public void preInit(){
		KeyBinds.defaults(
				"up", Keys.W,
				"left", Keys.A,
				"down", Keys.S,
				"right", Keys.D,
				"rotate", Keys.R,
				"menu", Keys.ESCAPE
			);
			
		Settings.loadAll("io.anuke.moment");
		
		for(int x = 0; x < size; x ++){
			for(int y = 0; y < size; y ++){
				tiles[x][y] = new Tile(x, y, TileType.grass);
			}
		}
		
		player = new Player();
		
	}
	
	@Override
	public void postInit(){
		restart();
	}
	
	@Override
	public void update(){
		if(!paused)
			super.update();
		
		if(!playing || paused) return;
		
		//if(UInput.keyUp(Keys.Q))
		//	System.out.println("Enemies: " + Enemy.amount + " Wavetime: " + wavetime + " Wave: " + wave + " Wavespace: " + wavespace);
		
		//System.out.println(Enemy.amount);
		if(Enemy.amount <= 0)
			wavetime -= delta();
		
		if(wavetime <= 0/* || UInput.keyUp(Keys.Q)*/){
			runWave();
		}
	}
	
	public void play(){
		wavetime = waveSpacing();
		
		if(showedTutorial){
			playing = true;
			paused = false;
		}else{
			playing = true;
			paused = true;
			get(UI.class).tutorial.show(get(UI.class).scene);
			showedTutorial = true;
		}
	}
	
	public void restart(){
		wave = 1;
		wavetime = waveSpacing();
		Entities.clear();
		Enemy.amount = 0;
		player.add();
		player.heal();
		items.clear();
		generate();
		
		player.x = core.worldx();
		player.y = core.worldy()-8;
		
		items.put(Item.stone, 20);
		
		//items.put(Item.stone, 2000);
		//items.put(Item.iron, 2000);
		//items.put(Item.steel, 2000);
		
		if(get(UI.class).about != null)
		get(UI.class).updateItems();
	}
	
	public void coreDestroyed(){
		Effects.shake(5, 6);
		USound.play("corexplode");
		for(int i = 0; i < 16; i ++){
			Timers.run(i*2, ()->{
				Effects.effect("explosion", core.worldx()+Mathf.range(40), core.worldy()+Mathf.range(40));
			});
			
		}
		Effects.effect("coreexplosion", core.worldx(), core.worldy());
		
		Timers.run(60, ()->{
			getModule(UI.class).showRestart();
		});
	}
	
	void generate(){
		spawnpoints.clear();
		Pathfind.reset();
		Generator.generate(tiles, "map");
		Pathfind.updatePath();
		core.setBlock(TileType.core);
		int x = core.x, y = core.y;
		
		set(x, y-1, TileType.conveyor, 1);
		set(x, y-2, TileType.conveyor, 1);
		set(x, y-3, TileType.conveyor, 1);
		set(x, y-4, TileType.stonedrill, 0);
		//just in case
		tiles[x][y-4].setFloor(TileType.stone);
		
		
		tiles[x+2][y-2].setFloor(TileType.stone);
		set(x+2, y-2, TileType.stonedrill, 0);
		set(x+2, y-1, TileType.conveyor, 1);
		set(x+2, y, TileType.turret, 0);
		
		tiles[x-2][y-2].setFloor(TileType.stone);
		set(x-2, y-2, TileType.stonedrill, 0);
		set(x-2, y-1, TileType.conveyor, 1);
		set(x-2, y, TileType.turret, 0);
	}
	
	void set(int x, int y, TileType type, int rot){
		tiles[x][y].setBlock(type);
		tiles[x][y].rotation = rot;
	}
	
	public void runWave(){
		int amount = wave;
		USound.play("spawn");
		
		for(int i = 0; i < amount; i ++){
			int pos = i;
			
			for(int w = 0; w < spawnpoints.size; w ++){
				int point = w;
				Tile tile = spawnpoints.get(w);
				
				Timers.run(i*30f, ()->{
					
					Enemy enemy = null;
					
					if(wave%5 == 0 /*&& point == 1 */&& pos == 0){
						enemy = new BossEnemy(point);
					}else if(wave > 3 && pos < amount/2){
						enemy = new FastEnemy(point);
					}else if(wave > 8 && pos % 3 == 0 && wave%2==1){
						enemy = new FlameEnemy(point);
					}else{
						enemy = new Enemy(point);
					}
					
					enemy.set(tile.worldx(), tile.worldy());
					Effects.effect("spawn", enemy);
					enemy.add();
				});
			}
		}
		
		wave ++;
		
		wavetime = waveSpacing();
	}
	
	public float waveSpacing(){
		int scale = Settings.getInt("difficulty");
		float out = (scale == 0 ? 2f : scale == 1f ? 1f : 0.5f);
		return wavespace*out;
	}
	
	public Tile tile(int x, int y){
		if(!Mathf.inBounds(x, y, tiles)) return null;
		return tiles[x][y];
	}
	
	public void addItem(Item item, int amount){
		items.put(item, items.get(item, 0)+amount);
		get(UI.class).updateItems();
	}
	
	public boolean hasItems(ItemStack[] items){
		for(ItemStack stack : items)
			if(!hasItem(stack))
				return false;
		return true;
	}
	
	public boolean hasItem(ItemStack req){
		return items.get(req.item, 0) >= req.amount; 
	}
	
	public void removeItem(ItemStack req){
		items.put(req.item, items.get(req.item, 0)-req.amount);
		get(UI.class).updateItems();
	}
}
