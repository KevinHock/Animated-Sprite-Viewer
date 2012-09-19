package animated_sprite_viewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * The WhitespaceFreeXMLNode class works hand in hand with the corresponding
 * Doc class. This class represents a single node in that XML tree. These
 * classes make our life easier when dealing with xml documents. XML documents
 * are basically trees, this organizes them formally as such.
 * 
 * @author  Richard McKenna &
 *          Debugging Enterprises
 * @version 1.0
 */
public class WhitespaceFreeXMLNode 
{
    // NAME OF THIS NODE
    private String name;
    
    // DATA FOR THIS NODE
    private String data;

    // CHILD NODES
    private ArrayList<WhitespaceFreeXMLNode> children;

    // ATTRIBUTES
    private HashMap<String, String> attributes;
    
    /**
     * Constructs a node only with a name. Children, attributes, and
     * data can be added later.
     * 
     * @param initName Name of this node.
     */
    public WhitespaceFreeXMLNode(String initName)
    {
        name = initName;
        data = "";
        children = new ArrayList<WhitespaceFreeXMLNode>();
        attributes = new HashMap<String, String>();
    }

    // ACCESSOR METHODS

    /**
     * Accessor method for getting the name of this node.
     * 
     * @return The name of this node in the xml file. All nodes must have names.
     */
    public String getName() { return name; }
    
    /**
     * Accessor method for getting the data of this node.
     * 
     * @return The data for this node in the xml file. Not all nodes have data.
     * Should a node not have data, null is returned.
     */
    public String getData() { return data; }

    /**
     * Accessor method for getting all the children nodes of this node.
     * 
     * @return An Iterator for getting each child node of this node.
     */
    public Iterator<WhitespaceFreeXMLNode> getChildren() { return children.iterator(); }
    
    /**
     * Accessor method for getting the value of a given attribute for this node.
     * 
     * @param attributeName Name of the attribute whose value is being requested.
     * 
     * @return The value that corresponds to the attributeName requested.
     */
    public String getAttributeValue(String attributeName)
    {
        return attributes.get(attributeName);
    }
    
    /**
     * Accessor method for getting all the attributes one at a time via
     * an iterator.
     * 
     * @return An iterator for going through all the attribute names.
     */
    public Iterator<String> getAttributeNamesIterator() { return attributes.keySet().iterator(); }

    /**
     * A search method for returning all the child nodes named type.
     * 
     * @param type The name of the child nodes to search for.
     * 
     * @return A list of all the child nodes of this node with a name of type.
     */
    public ArrayList<WhitespaceFreeXMLNode> getChildrenOfType(String type)
    {
        // WE'LL PUT THE CHILD NODES THAT FIT THE DESCRIPTION IN HERE
        ArrayList<WhitespaceFreeXMLNode> foundNodes = new ArrayList<WhitespaceFreeXMLNode>();
        
        // GO THROUGH ALL THE NODES
        for (WhitespaceFreeXMLNode node : children)
        {
            // IS IT A MATCH?
            if (node.getName().equals(type))
            {
                // YES SO ADD IT
                foundNodes.add(node);
            }
        }
        // RETURN THE LIST FULL OF THE FOUND MATCHES
        return foundNodes;
    }

    /**
     * A search method for getting a single child node of this node
     * that has a name of type.
     * 
     * @param type The name of the child node to look for.
     * 
     * @return The found child node.
     */
    public WhitespaceFreeXMLNode getChildOfType(String type)
    {
        ArrayList<WhitespaceFreeXMLNode> foundNodes = getChildrenOfType(type);
        if (foundNodes.size() > 0)
            return foundNodes.get(0);
        else
            return null;
    }
    
    /**
     * Adds a child node to this node.
     * 
     * @param nodeToAdd Child node to add to this node.
     */
    public void addChild(WhitespaceFreeXMLNode nodeToAdd)
    {
        children.add(nodeToAdd);
    }

    /**
     * Adds an attribute to this node.
     * 
     * @param attributeName The name of the attribute to add.
     * 
     * @param attributeValue The value of the attribute to add.
     */
    public void addAttribute(String attributeName, String attributeValue)
    {
        attributes.put(attributeName, attributeValue);
    }
    
    /**
     * Mutator method for setting the data of this node.
     * 
     * @param initData The data to set for this node.
     */
    public void setData(String initData)
    {
        data = initData;
    }
}