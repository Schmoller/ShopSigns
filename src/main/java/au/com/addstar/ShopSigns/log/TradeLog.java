package au.com.addstar.ShopSigns.log;

import java.sql.*;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import au.com.addstar.ShopSigns.ShopSignsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class TradeLog
{
	private static TradeLog mInstance;
	private static final Integer dbVersion = 1;
	
	public static void log(Player player, String type, String action, double money, int count, String data, String extra, Location location)
	{
		TradeLogEntry entry = new TradeLogEntry(player.getName(), type, action, money, count, data, extra, location);
		if(mInstance != null)
			mInstance.log(entry);
	}
	
	public static void log(Player player, OfflinePlayer otherPlayer, String type, String action, double money, int count, String data, String extra, Location location)
	{
		TradeLogEntry entry = new TradeLogEntry(player.getName(), otherPlayer.getName(), type, action, money, count, data, extra, location);
		if(mInstance != null)
			mInstance.log(entry);
	}
	
	public static void log(Player player, String otherPlayer, String type, String action, double money, int count, String data, String extra, Location location)
	{
		TradeLogEntry entry = new TradeLogEntry(player.getName(), otherPlayer, type, action, money, count, data, extra, location);
		if(mInstance != null)
			mInstance.log(entry);
	}
	
	private final String mUsername;
	private final String mPassword;
    private final String mDatabase;
	private final String mHost;
	private final String mUseSSL;
	
	private Connection mConnection;
	private PreparedStatement mInsertStatement;
	
	private final BlockingQueue<TradeLogEntry> mCachedEntries;
	private QueueThread mThread;
	
	public TradeLog(String host, String database, String username, String password, String useSSL)
	{
		mHost = host;
		mDatabase = database;
		mUsername = username;
		mPassword = password;
		mUseSSL = useSSL;
		
		mCachedEntries = new LinkedBlockingQueue<>();
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
		catch ( InterruptedException ignored)
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
			Properties properties = new Properties();
			properties.put("useSSL", mUseSSL);
			properties.put("password", mPassword);
			properties.put("username", mUsername);
			mConnection = DriverManager.getConnection(String.format("jdbc:mysql://%s/%s", mHost, mDatabase),properties);
			
			mInsertStatement = mConnection.prepareStatement("INSERT `TradeLog` VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
		}
		catch(ClassNotFoundException e)
		{
			throw new SQLException("MySQL not found");
		}
	}
	
	private void prepareTables() throws SQLException
	{
		try( Statement statement = mConnection.createStatement())
		{
			statement.executeQuery("SELECT * from `TradeLog` LIMIT 0;");
		}
		catch(SQLException e) {
            // Table not there, procede
            try(Statement statement = mConnection.createStatement()) {
                statement.executeUpdate("CREATE TABLE `TradeLog` (`id` bigint(20) AUTO_INCREMENT PRIMARY KEY, `date` datetime NOT NULL, `type` varchar(15) NOT NULL, `action` varchar(15) NOT NULL, `player` varchar(20) NOT NULL, `otherplayer` varchar(20), `quantity` smallint(6), `amount` float(10,2) NOT NULL, `data` varchar(20), `extra` varchar(20), `server` varchar(20) NOT NULL, `world` varchar(20) NOT NULL, `location` varchar(30) NOT NULL);");
            }catch (SQLException e2){
                e2.printStackTrace();
            }
        }
		
		try(Statement statement = mConnection.createStatement()){
            ResultSet set = statement.executeQuery("Select MAX(`version`) from `Versions`;");
            set.next();
            Integer current = set.getInt(1);
            if(current < dbVersion)updateDatabase(current);
        }catch (SQLException e){
            updateDatabase(0);
        }
		
	}
	
	private void updateDatabase(Integer currentVersion){
        if(currentVersion == null)currentVersion = 0;
        if(currentVersion.equals(dbVersion))return;
	    switch (currentVersion){
            case 0:
                try(    Statement statement = mConnection.createStatement();
                        Statement s2 = mConnection.createStatement() ) {
                    statement.executeUpdate("ALTER TABLE TradeLog modify extra varchar(100) null;");
                    s2.executeUpdate("CREATE TABLE 'Versions' (`version` int(2));");
                }catch (SQLException e ){
                    Bukkit.getLogger().warning(" SHOPSIGNS: COULD NOT UPDATE VERSIONS!!! DB ERRORS:" +e.getMessage());
                    break;
                }
        
            case 1:
                //current version
                break;
        }
        //at this point everything should be updated to latest versions.
        try( Statement statement = mConnection.createStatement() ){
            statement.executeUpdate("INSERT `Versions` VALUES ("+dbVersion+") ;");
            Bukkit.getLogger().warning("[SHOPSIGNS]: DB UPDATED TO VERSION: " +dbVersion);
        }catch (SQLException e){
            Bukkit.getLogger().warning(" SHOPSIGNS: COULD NOT UPDATE VERSIONS!!! DB ERRORS: " + e.getMessage() );
            
        }
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
			catch(InterruptedException ignored)
			{
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
}
