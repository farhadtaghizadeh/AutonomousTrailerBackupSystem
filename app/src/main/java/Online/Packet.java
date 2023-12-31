package Online;





import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

/**
 *
 * Packets are what the sockets in this program will send to each other
 *
 * @author Joshua Bergthold
 */
public final class Packet implements Serializable {
    /**
     * String that represents what the server should do with the data field
     * command = "{messageType};{dataType};{TBI...}" -> commands = ["messageType;", "dataType;", "TBI...;"]
     */
    private final Command command;


    /**
     * Data to be processed by recipient in some way (often specified by the command)
     */
    private final Object data;

    /**
     * ID of the author of this packet
     */
    private final UUID authorID;

    /**
     * ID of this packet
     */
    private final UUID packetID;




    /**
     * Creates a packet with the specified parameters
     * @param command String that represents what the server should do with the data field
     * @param data data to be processed in some way (often specified by the command)
     * @param authorID id of the author of this packet
     */
     public Packet(String command, Object data, UUID authorID) {
        this(new Command(command), data, authorID);
    }

    /**
     * Creates a packet with the specified parameters
     * @param c Command object that contains a String command
     * @param data data to be processed in some way (often specified by the command)
     * @param authorID id of the author of this packet
     */
    public Packet(Command c, Object data, UUID authorID){
        this(c,data,authorID, UUID.randomUUID());
    }

    private Packet(Command c, Object data, UUID authorID, UUID packetID){
        this.command = c;
        this.authorID = authorID;
        this.packetID = packetID;
        this.data = data;
    }

    /**
     * creates a simple text message with type SIMPLE_TEXT_MESSAGE
     * @param message Text message to be contained in this packet
     * @param authorID id of the author of this packet
     */
    public Packet(String message, UUID authorID){
        this(new Command(DefaultOnlineCommands.SIMPLE_TEXT), message, authorID);
    }







    /**
     * @return the list of commands that this packet holds
     */
    public Command getCommand(){
        return command;
    }

    /**
     * @return data of the packet
     */
    public Object getData() {
        return data;
    }



    /**
     * @return author ID of packet
     */
    public UUID getAuthorID(){
        return this.authorID;
    }

    /**
     * @return ID of this packet
     */
    public UUID getPacketID(){
        return this.packetID;
    }


    @Override
    public String toString(){
        return toShortenedString();
    }


    public String toJSONString(){
        return String.format("{\"command\":\"%s\", \"data\":\" %s\", \"packetID\":\"%s\", \"authID\":\"%s\"}", command.toString(), data, packetID, authorID);
    }

    public static String getDataFromJSONString(String jString, String field){
        int start = jString.indexOf(field) + field.length() + 3;
        return jString.substring(start, start + jString.substring(start).indexOf('"'));
    }
    /*
    public static Map<String, Object> getMapFromjString(String jString){
        String currString = jString;
        while (jString.length() != 0){
            Pair<String,String> strings = getFirstWordInQuotes(currString);
            currString = strings.second;

        }
    }

    public static Pair<String,String> getFirstWordInQuotes(String s){
        int start = s.indexOf('"') + 1;
        int end = s.substring(start).indexOf('"');
        if(start == 0 || end == -1){
            return new Pair("", "");
        }
        return new Pair(s.substring(start,end), s.substring(end+1));
    }*/
    public static Packet fromJSONString(String jString){
        String jsonData = getDataFromJSONString(jString,"data");
        Command c = new Command(getDataFromJSONString(jString,"command"));
        UUID authID = getUUIDFromString(getDataFromJSONString(jString, "authID"));
        UUID packetID = getUUIDFromString(getDataFromJSONString(jString,"packetID"));
            try {
                double data = Double.parseDouble(jsonData);
                return new Packet(c,data,authID,packetID);
            } catch(NumberFormatException e){
                return new Packet(c,jsonData,authID,packetID);
            }
    }

    private static UUID getUUIDFromString(String id){
        if(id == null || id.equals("null")){
            return null;
        }
        return UUID.fromString(id);
    }
    public String toLongString(){
        return "Packet{PacketID: " + this.packetID + ", Command: " + command.getCommandString() + ", Data: " + this.data +
                ", AuthID: " + this.authorID + ", Type: " + command.getTypeString() + "}";
    }

    /**
     * A short version of the String representation of this packet
     * @return short string version of this packet
     */
    public String toShortenedString(){
        return "Packet{PacketID: " + shortenedID(this.packetID) +
                ", DataType: " + (data != null? this.data.getClass(): "null") +
                ", AuthID: " + shortenedID(this.authorID) +
                ", Type: " + command.getTypeString() + "}";
    }

    /**
     * Shortened author ID of this packet
     * @return shortened ID
     */
    public String shortAuthID(){
        return shortenedID(authorID);
    }

    /**
     * @return The type of this packet
     */
    public String getType(){
        if(command == null){
            return "null";
        }
        return command.getType();
    }

    /**
     * Helper method that takes a UUID and returns a shortened version of it
     * @param id full iD
     * @return String version of the shortened ID
     */
    public static String shortenedID(UUID id){
        if(id == null){
            return "null";
        }
        String str = id.toString();
        return str.substring(str.length()-12);
    }
}


