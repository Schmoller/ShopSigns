package au.com.addstar.ShopSigns.log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TradeLog
{
	private static TradeLog mInstance;
	
	public static void log(Player player, String type, String action, double money, int count, String data, String extra, Location location)
	{
		TradeLogEntry entry = new TradeLogEntry(player.getName(), type, action, money, count, data, extra, location);
		if(mInstance != null)
			mInstance.log(entry);
	}
	
	public static void log(Player player, Player otherPlayer, String type, String action, double money, int count, String data, String extra, Location location)
	{
		TradeLogEntry entry = new TradeLogEntry(player.getName(), otherPlayer.getName(), type, action, money, count, data, extra, location);
		if(mInstance != null)
			mInstance.log(entry);
	}
	
	private String mUsername;
	private String mPassword;
	private String mHost;
	private String mDatabase;
	
	private Connection mConnection;
	private PreparedStatement mInsertStatement;
	
	private BlockingQueue<TradeLogEntry> mCachedEntries;
	private QueueThread mThread;
	
	public TradeLog(String host, String database, String username, String password)
	{
		mHost = host;
		mDatabase = database;
		mUsername = username;
		mPassword = password;
		
		mCachedEntries = new LinkedBlockingQueue<TradeLogEntry>();
	}
	
	public void initialize() throws IllegalArgumentException
	{
		try
		{
			mInstance = this;
			connect();
			mThread = new QueueThread();
			mThread.start();
		}
		catch(SQLException e)
		{
			throw new IllegalArgumentException(e);
		}
	}
	
	public void shutdown() throws SQLException
	{
		mInstance = null;
		mThread.interrupt();
		try
		{
			mThread.join();
		}
		catch ( InterruptedException e )
		{
		}
		closeAll();
	}
	
	private void closeAll() throws SQLException
	{
		mInsertStatement.close();
		mConnection.close();
	}
	
	public void log(TradeLogEntry entry)
	{
		mCachedEntries.offer(entry);
	}
	
	private void connect() throws SQLException
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			mConnection = DriverManager.getConnection(String.format("jdbc:mysql://%s/%s", mHost, mDatabase), mUsername, mPassword);
			
			mInsertStatement = mConnection.prepareStatement("INSERT `TradeLog` VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
		}
		catch(ClassNotFoundException e)
		{
			throw new SQLException("MySQL not found");
		}
	}
	
	private void prepareTables() throws SQLException
	{
		Statement statement = mConnection.createStatement();
		try
		{
			statement.executeQuery("SELECT * from `TradeLog` LIMIT 0;").close();
			return;
		}
		catch(SQLException e)
		{
			// Table not there, procede
		}
		
		statement.executeUpdate("CREATE TABLE `TradeLog` (`id` bigint(20) AUTO_INCREMENT PRIMARY KEY, `date` datetime NOT NULL, `type` varchar(15) NOT NULL, `action` varchar(15) NOT NULL, `player` varchar(20) NOT NULL, `otherplayer` varchar(20), `quantity` smallint(6), `amount` float(10,2) NOT NULL, `data` varchar(20), `extra` varchar(20), `server` varchar(20) NOT NULL, `world` varchar(20) NOT NULL, `location` varchar(30) NOT NULL);");
		
		statement.close();
	}
	
	private class QueueThread extends Thread
	{
		public QueueThread()
		{
			super("TradeLog Thread");
		}
		
		public void run()
		{
			try
			{
				prepareTables();
				
				while(true)
				{
					if(mCachedEntries.isEmpty() && isInterrupted())
						break;
					
					TradeLogEntry entry = mCachedEntries.take();
					
					try
					{
						mInsertStatement.setTimestamp(1, new Timestamp(entry.date));
						mInsertStatement.setString(2, entry.type);
						mInsertStatement.setString(3, entry.action);
						mInsertStatement.setString(4, entry.player);
						mInsertStatement.setString(5, entry.otherPlayer);
						mInsertStatement.setInt(6, entry.quantity);
						mInsertStatement.setDouble(7, entry.money);
						mInsertStatement.setString(8, entry.data);
						mInsertStatement.setString(9, entry.extra);
						mInsertStatement.setString(10, Bukkit.getServerName());
						mInsertStatement.setString(11, entry.location.getWorld().getName()); // world
						mInsertStatement.setString(12, String.format("%d;%d;%d", entry.location.getBlockX(), entry.location.getBlockY(), entry.location.getBlockZ())); // loc string
						
						mInsertStatement.executeUpdate();
					}
					catch(SQLException e)
					{
						e.printStackTrace();
						// Handle timeouts
						if(!isInterrupted() && mConnection.isClosed())
						{
							closeAll();
							connect();
							mCachedEntries.offer(entry);
						}
					}
				}
			}
			catch(InterruptedException e)
			{
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
}
