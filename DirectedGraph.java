
public class DirectedGraph {
  public static class EdgeNode {
    boolean myVisited;
    int myVertex;
    int myWeight;
    EdgeNode myNext;

    /**
     * javadoc×¢ÊÍÄÚÈÝ
     * 
     * @since 1.0
     * @version 1.1
     * @author xxx
     */
    public EdgeNode(int vertex, int weight, EdgeNode next) {
      myVertex = vertex;
      myWeight = weight;
      myNext = next;
      myVisited = false;
    }
  }

  public class VertexNode {
    String myData;
    int myCount;
    EdgeNode myFirstEdge;

    /**
     * javadoc×¢ÊÍÄÚÈÝ
     * 
     * @since 1.0
     * @version 1.1
     * @author xxx
     */
    public VertexNode(String data) {
      myData = data;
      myCount = 0;
      myFirstEdge = null;
    }
  }

  VertexNode[] vexList;

  /**
   * javadoc×¢ÊÍÄÚÈÝ
   * 
   * @since 1.0
   * @version 1.1
   * @author xxx
   */
  public DirectedGraph(String[] filteredWords,
      Edge[] edges) {
    vexList = new VertexNode[filteredWords.length];
    int count = 0;

    for (String word : filteredWords) {
      vexList[count] = new VertexNode(filteredWords[count]);
      count++;
    }

    for (Edge edge : edges) {
      VertexNode src = vexList[edge.mySrc];
      src.myFirstEdge = new EdgeNode(edge.myDest,
          edge.myWeight, src.myFirstEdge);
      src.myCount++;
    }
  }

  /**
   * javadoc×¢ÊÍÄÚÈÝ
   * 
   * @since 1.0
   * @version 1.1
   * @author xxx
   */
  public int getIndexOfWord(String word) {
    int count = 0;
    for (VertexNode vvNode : vexList) {
      if (word.equals(vvNode.myData)) {
        return count;
      } else {
        count++;
      }
    }
    return -1;
  }

  /**
   * javadoc×¢ÊÍÄÚÈÝ
   * 
   * @since 1.0
   * @version 1.1
   * @author xxx
   */
  public void resetVisited() {
    for (VertexNode vvNode : vexList) {
      EdgeNode eeNode = vvNode.myFirstEdge;
      while (eeNode != null) {
        eeNode.myVisited = false;
        eeNode = eeNode.myNext;
      }
    }
  }
}