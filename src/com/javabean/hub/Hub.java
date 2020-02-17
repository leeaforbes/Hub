package com.javabean.hub;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Hub extends JavaPlugin{
	//spawn location when joining server
	public static Location hubSpawn;
	//spawn hub spawn enabled
	public static boolean hubEnabled;
	
	//bound name, bounds
	public static HashMap<String, Bounds> boundsList = new HashMap<String, Bounds>();
	public static HashMap<String, Player> playersInBoundsList = new HashMap<String, Player>();

	public static Material[] wallSignTypes = {Material.OAK_WALL_SIGN, Material.ACACIA_WALL_SIGN, Material.BIRCH_WALL_SIGN, Material.DARK_OAK_WALL_SIGN, Material.JUNGLE_WALL_SIGN, Material.SPRUCE_WALL_SIGN};
	
	//creates instance of inner class GameTimer
	BoundsTimer boundsTimer = new BoundsTimer();
	
	//TODO create Bounds class that has a map of all players in that bound
	//every few seconds check for players in or out of bounds
	//on join, leave, and sign clicks? their bounds may change
	
	@Override
	public void onEnable(){
		//read various game data from XML file
		parseXMLGameData();
		
		//read bounds data from XML file
		parseBoundsData();
		
		//creates commands
		getCommand("hub").setExecutor(new HubCommand());
		getCommand("hub").setTabCompleter(new HubCommandTabCompleter());
		
		//event listener
		getServer().getPluginManager().registerEvents(new HubListener(), this);
		
		//plugin enabled successfully
		getLogger().info("------------");
		getLogger().info(getClass().getSimpleName() + " enabled!");
		getLogger().info("------------");
		
		//runs task immediately and every 5 ticks
		boundsTimer.runTaskTimer(this, 0, 100);
	}
	
	// Fired when plugin is disabled
	@Override
	public void onDisable(){
		//rewrite game data to XML file
		writeGameDataToXML();
		
		//rewrite bounds data to XML file
		writeBoundsDataToXML();
		
		//plugin disabled successfully
		getLogger().info("-------------");
		getLogger().info(getClass().getSimpleName() + " disabled!");
		getLogger().info("-------------");
	}
	
	//inner class for game timer
	class BoundsTimer extends BukkitRunnable{
		
		@Override
		public void run(){
			playersInBoundsList.clear();
			for(String boundsName : boundsList.keySet()){
				Bounds bounds = boundsList.get(boundsName);
				bounds.emptyPlayers();
				for(Player player : getServer().getOnlinePlayers()){
					if(!playersInBoundsList.containsKey(player.getName()) && bounds.isLocationInBounds(player.getLocation())){
						bounds.addPlayer(player);
						playersInBoundsList.put(player.getName(), player);
//						getLogger().info("Added " + player.getName() + " to bound " + bounds.getName());
					}
				}
			}
		}
	}
	
	public static void updateBoundStatus(Player player){
		for(String boundsName : boundsList.keySet()){
			Bounds bounds = boundsList.get(boundsName);
			if(bounds.containsPlayer(player)){
				bounds.addPlayer(player);
				playersInBoundsList.put(player.getName(), player);
			}
		}
	}
	
	private void parseXMLGameData(){
		//set up file location
		String arenaFileName = "gamedata.xml";
		File arenaInfoFile = getDataFolder();
		if(arenaInfoFile.mkdir()){
			getLogger().info("Created \\CaptureTheFlag directory.");
		}
		
		arenaInfoFile = new File(arenaInfoFile.toString() + "\\" + arenaFileName);
		try {
			if(arenaInfoFile.createNewFile()){
				getLogger().info("Created new " + arenaFileName + " file.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<World> worlds = Bukkit.getWorlds();
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		Document doc = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(arenaInfoFile);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		doc.getDocumentElement().normalize();
		
		//get various game data
		Node hubNode = doc.getElementsByTagName("hubspawn").item(0);
		if (hubNode.getNodeType() == Node.ELEMENT_NODE) {
			Element hubElement = (Element)hubNode;
			String worldName = hubElement.getAttribute("world");
			hubSpawn = new Location(getWorldOfName(worlds, worldName),
					Double.parseDouble(hubElement.getAttribute("x")),
					Double.parseDouble(hubElement.getAttribute("y")),
					Double.parseDouble(hubElement.getAttribute("z")));
			hubEnabled = Boolean.parseBoolean(hubElement.getAttribute("enabled"));
		}
	}
	
	public void writeGameDataToXML(){
		//set up file location
		String arenaFileName = "gamedata.xml";
		File arenaInfoFile = getDataFolder();
		if(arenaInfoFile.mkdir()){
			getLogger().info("Created \\CaptureTheFlag directory.");
		}
		
		arenaInfoFile = new File(arenaInfoFile.toString() + "\\" + arenaFileName);
		try {
			if(arenaInfoFile.createNewFile()){
				getLogger().info("Created new " + arenaFileName + " file.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//write to XML
		try {
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			// root elements
			Document doc = docBuilder.newDocument();
			
			//create data root tag
			Element dataRootElement = doc.createElement("data");
			doc.appendChild(dataRootElement);
			Element hubSpawnElement = doc.createElement("hubspawn");
			hubSpawnElement.setAttribute("world", hubSpawn.getWorld().getName());
			hubSpawnElement.setAttribute("x", "" + hubSpawn.getX());
			hubSpawnElement.setAttribute("y", "" + hubSpawn.getY());
			hubSpawnElement.setAttribute("z", "" + hubSpawn.getZ());
			hubSpawnElement.setAttribute("yaw", "" + hubSpawn.getYaw());
			hubSpawnElement.setAttribute("pitch", "" + hubSpawn.getPitch());
			hubSpawnElement.setAttribute("enabled", "" + hubEnabled);
			dataRootElement.appendChild(hubSpawnElement);
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(arenaInfoFile);
			
			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);
			
			transformer.transform(source, result);
		}
		catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		}
		catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}
	
	private void parseBoundsData(){
		//set up file location
		String arenaFileName = "bounddata.xml";
		File arenaInfoFile = getDataFolder();
		if(arenaInfoFile.mkdir()){
			getLogger().info("Created \\CaptureTheFlag directory.");
		}
		
		arenaInfoFile = new File(arenaInfoFile.toString() + "\\" + arenaFileName);
		try {
			if(arenaInfoFile.createNewFile()){
				getLogger().info("Created new " + arenaFileName + " file.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<World> worlds = Bukkit.getWorlds();
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		Document doc = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(arenaInfoFile);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		doc.getDocumentElement().normalize();
		
		//get bounds data
		NodeList boundList = doc.getElementsByTagName("bound");
		for (int boundIndex = 0; boundIndex < boundList.getLength(); boundIndex++) {
			Node boundNode = boundList.item(boundIndex);
			if (boundNode.getNodeType() == Node.ELEMENT_NODE) {
				Element boundElement = (Element)boundNode;
				String worldName = boundElement.getAttribute("world");
				World world = getWorldOfName(worlds, worldName);
				Location bound1 = new Location(world,
						Double.parseDouble(boundElement.getAttribute("x1")),
						Double.parseDouble(boundElement.getAttribute("y1")),
						Double.parseDouble(boundElement.getAttribute("z1")));
				Location bound2 = new Location(world,
						Double.parseDouble(boundElement.getAttribute("x2")),
						Double.parseDouble(boundElement.getAttribute("y2")),
						Double.parseDouble(boundElement.getAttribute("z2")));
				Bounds bounds = new Bounds(bound1, bound2, boundElement.getAttribute("name"));
				boundsList.put(bounds.getName(), bounds);
			}
		}
	}
	
	public void writeBoundsDataToXML(){
		//set up file location
		String arenaFileName = "bounddata.xml";
		File arenaInfoFile = getDataFolder();
		if(arenaInfoFile.mkdir()){
			getLogger().info("Created \\CaptureTheFlag directory.");
		}
		
		arenaInfoFile = new File(arenaInfoFile.toString() + "\\" + arenaFileName);
		try {
			if(arenaInfoFile.createNewFile()){
				getLogger().info("Created new " + arenaFileName + " file.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//write to XML
		try {
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			// root elements
			Document doc = docBuilder.newDocument();
			
			//create data root tag
			Element dataRootElement = doc.createElement("data");
			doc.appendChild(dataRootElement);
			for(String boundsName : boundsList.keySet()){
				Bounds bounds = boundsList.get(boundsName);
				Location bound1 = bounds.getBound1();
				Location bound2 = bounds.getBound2();
				Element boundsElement = doc.createElement("bound");
				boundsElement.setAttribute("name", bounds.getName());
				boundsElement.setAttribute("world", bounds.getBound1().getWorld().getName());
				boundsElement.setAttribute("x1", "" + bound1.getX());
				boundsElement.setAttribute("y1", "" + bound1.getY());
				boundsElement.setAttribute("z1", "" + bound1.getZ());
				boundsElement.setAttribute("x2", "" + bound2.getX());
				boundsElement.setAttribute("y2", "" + bound2.getY());
				boundsElement.setAttribute("z2", "" + bound2.getZ());
				dataRootElement.appendChild(boundsElement);
			}
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(arenaInfoFile);
			
			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);
			
			transformer.transform(source, result);
		}
		catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		}
		catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}
	
	private World getWorldOfName(List<World> worlds, String name){
		for(World world : worlds){
			if(world.getName().equals(name)){
				return world;
			}
		}
		return null;
	}
	
	public static boolean isASign(Material materialInQuestion){
		for(Material wallSignType : wallSignTypes){
			if(wallSignType == materialInQuestion){
				return true;
			}
		}
		return false;
	}
}
