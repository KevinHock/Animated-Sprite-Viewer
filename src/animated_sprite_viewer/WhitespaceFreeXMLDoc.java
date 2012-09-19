package animated_sprite_viewer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The WhitespaceFreeXMLDoc class makes our life easier. It stores
 * all the information about an XML document in a tree that's easy
 * to navigate and contains zero whitespace, which is good for XML
 * files used for storing data, since whitespace is irrelevant to
 * such documents and simply clutters loaded trees with this data.
 * 
 * @author  Richard McKenna &
 *          Debugging Enterprises
 * @version 1.0
 */
public class WhitespaceFreeXMLDoc 
{
    // ROOT OF THE TREE
    private WhitespaceFreeXMLNode root;

    /**
     * Default constructor, this does not initialization. The loadDoc
     * method is where everything is loaded from.
     */
    public WhitespaceFreeXMLDoc()
    {
        root = null;
    }

    /**
     * Accessor method for getting the root of the tree to allow
     * one to climb the tree.
     * 
     * @return The root of this tree, which would be the root of
     * the xml document.
     */
    public WhitespaceFreeXMLNode getRoot() { return root; }

    /**
     * This method starts all the important work. It extracts all the
     * information found in the doc argument, and loads it (whitespace-free)
     * into this doc.
     * 
     * @param doc The loaded xml document with all the data that should
     * be loaded into this object.
     */
    public void loadDoc(Document doc)
    {
        // ROOT OF THE TREE
        Element rootElement = doc.getDocumentElement();
        Node rootNode = rootElement;
        String rootNodeName = rootNode.getNodeName();
        WhitespaceFreeXMLNode newRootNode = new WhitespaceFreeXMLNode(rootNodeName);
        
        // NOW START LOADING NODE DATA
        loadWhitespaceFreeNode(rootNode, newRootNode);        
        
        // IF EVERYTHING WORKED THEN KEEP THE WHOLE TREE
        root = newRootNode;
    }

    /**
     * This recursive method loads all the necessary data from the nodeToLoadFrom
     * node into the nodeToLoad node. This includes node attributes and children
     * and it recursively cascades node loading on down through the tree.
     * 
     * @param nodeToLoadFrom The node with the data that we wish to load.
     * 
     * @param nodeToLoad Our own node type that we will load.
     */
    private void loadWhitespaceFreeNode(Node nodeToLoadFrom, WhitespaceFreeXMLNode nodeToLoad)
    {
        // FIRST GET ALL THE ATTRIBUTES
        NamedNodeMap attributes = nodeToLoadFrom.getAttributes();
        int numAttributes = attributes.getLength();
        for (int i = 0; i < numAttributes; i++)
        {
            Node attribute = attributes.item(i);
            String name = attribute.getNodeName();
            String value = attribute.getNodeValue();
            nodeToLoad.addAttribute(name, value);
        }

        // NOW WE NEED TO GET THE DATA (IF THERE IS ANY)
        
        // FIRST THING WE WANT TO DO IS SEE HOW MANY CHILDREN WE HAVE
        NodeList children = nodeToLoadFrom.getChildNodes();
        int numChildren = children.getLength();
        
        // IF THERE IS ONE CHILD AND IT IS NON WHITESPACE TEXT, 
        // THEN THAT'S THE DATA ASSOCIATED WITH THIS NODE
        if (numChildren == 1)
        {
            // GET THE ONLY CHILD
            Node childNode = children.item(0);
            int nodeType = childNode.getNodeType();
            
            // IS IT TEXT?
            if (childNode.getNodeType() == Node.TEXT_NODE)
            {
                String textualData = childNode.getTextContent().trim();
                nodeToLoad.setData(textualData);
            }
            // OR IS IT AN ENTITY?
            // NOTE THAT WE'LL IGNORE EVERYTHING ELSE
            else if (childNode.getNodeType() == Node.ELEMENT_NODE)
            {
                String childNodeName = childNode.getNodeName();
                WhitespaceFreeXMLNode childNodeToAdd = new WhitespaceFreeXMLNode(childNodeName);
                nodeToLoad.addChild(childNodeToAdd);
                loadWhitespaceFreeNode(childNode, childNodeToAdd);
            }
        }
        // OTHERWISE WE NEED TO GO THROUGH ALL THE CHILD NODES
        // NOTE THAT THIS ENTITY NODE CAN'T HAVE TEXTUAL DATA
        // SINCE IT HAS CHILDREN
        else
        {
            for (int i = 0; i < numChildren; i++)
            {
                Node childNode = children.item(i);
                int nodeType = childNode.getNodeType();
         
                // WE ONLY CARE ABOUT ENTITY CHILD NODES
                if (nodeType == Node.ELEMENT_NODE)
                {
                    String childNodeName = childNode.getNodeName();
                    WhitespaceFreeXMLNode childNodeToAdd = new WhitespaceFreeXMLNode(childNodeName);
                    nodeToLoad.addChild(childNodeToAdd);
                    loadWhitespaceFreeNode(childNode, childNodeToAdd);
                }
            }
        }        
    }
}