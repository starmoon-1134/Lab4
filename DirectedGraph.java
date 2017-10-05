public class DirectedGraph {
    //modification 1 on B1
	public class EdgeNode {
		boolean m_visited;
		int m_vertex;
		int m_weight;
		EdgeNode m_next;
		
		public EdgeNode(int vertex, int weight, EdgeNode next) {
			m_vertex = vertex;
			m_weight = weight;
			m_next = next;
			m_visited = false;
		}
	}
	
	public class VertexNode {
		String m_data;
		int m_count;
		EdgeNode m_firstEdge;
		
		public VertexNode(String data) {
			m_data = data;
			m_count = 0;
			m_firstEdge = null;
		}
	}
	
	VertexNode[] vexList;
	
	public DirectedGraph(String[] filteredWords, Edge[] edges) {
		vexList = new VertexNode[filteredWords.length];
		int count = 0;
		
		for(String word : filteredWords) {
			vexList[count] = new VertexNode(filteredWords[count]);
			count++;
		}
		
		for(Edge edge : edges) {
			VertexNode src = vexList[edge.m_src];
			src.m_firstEdge = new EdgeNode(edge.m_dest, edge.m_weight, src.m_firstEdge);
			src.m_count++;
		}
	}
	
	public int getIndexOfWord(String word) {
		int count = 0;
		for(VertexNode vNode : vexList) {
			if(word.equals(vNode.m_data)) return count;
			else count++;
		}
		return -1;
	}
	
	public void resetVisited() {
		for(VertexNode vNode : vexList) {
			EdgeNode eNode = vNode.m_firstEdge;
			while(eNode != null) {
				eNode.m_visited = false;
				eNode = eNode.m_next;
			}
		}
	}
}
