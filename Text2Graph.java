import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Text2Graph {
	private DirectedGraph graph;

	public void init() throws FileNotFoundException {
		Scanner input = new Scanner(new File(Configuration.TextFilePath));

		String text = new String();
		while (input.hasNext()) {
			String line = input.nextLine();
			text += line + " ";
		}

		input.close();

		/* ���ƥ�䵽�ĵ�һ���ַ��Ƿ�Ӣ���ַ�������һ������ǰ������ȫ������ */
		Pattern p = Pattern.compile("[a-zA-Z]+");
		Matcher m = p.matcher(text);
		if (m.find()) {
			int firstLetterPos = m.start();
			if (firstLetterPos > 0) {
				text = text.substring(firstLetterPos);
			}
		}

		text = text.toLowerCase();

		String[] words = text.split("[^a-zA-Z]+");

		graph = generateGraph(words);
	}

	public String queryBridgeWords(String word1, String word2) {
		ArrayList<String> bridgeWords = queryBridgeWords(graph, word1, word2);
		String result = "";

		if (bridgeWords.size() == 0) {
			int indexOfWord1 = graph.getIndexOfWord(word1);
			int indexOfWord2 = graph.getIndexOfWord(word2);
			if (indexOfWord1 == -1 || indexOfWord2 == -1)
				result = "No " + word1 + " or " + word2 + " in the graph!";
			else
				result = "No bridge words from " + word1 + " to " + word2 + "!";
			return result;
		}

		result = "The bridge words from " + word1 + " to " + word2 + " are:";
		int count = 1;
		int size = bridgeWords.size();
		for (String word : bridgeWords) {
			if (count == 1)
				result += word;
			else if (count == size)
				result += " and " + word + ".";
			else
				result += ", " + word;
			count++;
		}

		return result;
	}

	public String generateNewText(String inputText) {
		return generateNewText(graph, inputText);
	}

	public String calcShortestPath(String word1, String word2) {
		if (word2.equals(""))
			return calcShortestPath(graph, word1);
		else
			return calcShortestPath(graph, word1, word2);
	}

	public void outputGraph() {
		outputGraph(graph);
	}

	public String randomWalk() {
		return randomWalk(graph);
	}

	/*
	 * ����Ӣ���ı����ж�Ӧ�б���������ͼ ������Ӣ���ı��Կո�Ϊ�ָ���ָ��������ĵ����б� ԭ��ʹ�� wordsHashMap ȥ���ظ����ʺ�����ַ�������
	 * filteredWords �У���Ϊ�������ݼ��ϣ����ֵ�����ԭ�ı��г��ֵ��Ⱥ���� �Ե���Ϊ�����Ե����� filteredWords
	 * �е��±�Ϊֵ�����wordsHashMap���Ա����ɱ�ʱʹ�á� ��ÿ�������ڵ�������Ϊ���飬�Ըô���Ϊ�����Ըô������ı��г��ֵĴ���Ϊֵ������
	 * phraseHashMap�� ���� phraseHashMap���Դ�����׸������ڶ��㼯���е��±���Ϊ�ߵ� src���Եڶ������ʵ��±���Ϊ�ߵ�
	 * dest���Դ�����ֵĴ�����Ϊ�ߵ� weight�������߼��ϡ� ���ö��㼯���Լ��߼��Ͻ�������ͼ�����ء�
	 */
	public static DirectedGraph generateGraph(String[] words) {
		HashMap wordsHashMap = new HashMap();

		// ȥ���ظ����ʣ��Ե���Ϊ�������ʳ��ֵĴ���Ϊֵ������ HashMap ��
		int count = 0;
		for (String word : words) {
			if (!wordsHashMap.containsKey(word)) {
				wordsHashMap.put(word, count);
				count++;
			}
		}

		String[] filteredWords = new String[wordsHashMap.size()];

		// ���� HashMap �ļ��������ظ��ĵ��ʼ��ϣ��Ե��ʶ�Ӧ��ֵ��Ϊ�±꣬�����ʴ����µ��ַ���������
		Set<String> wordsKeys = wordsHashMap.keySet();
		for (String key : wordsKeys) {
			int index = (int) wordsHashMap.get(key);
			filteredWords[index] = key;
		}

		// ��ÿ�������ڵ�������Ϊ���飬�Ըô���Ϊ�����Ըô������ı��г��ֵĴ���Ϊֵ������ phraseHashMap��
		HashMap phraseHashMap = new HashMap();
		for (int i = 0; i < words.length - 1; i++) {
			String phrase = words[i] + " " + words[i + 1];
			if (phraseHashMap.containsKey(phrase)) {
				int value = (int) phraseHashMap.get(phrase);
				phraseHashMap.put(phrase, value + 1);
			} else
				phraseHashMap.put(phrase, 1);
		}

		Edge[] edges = new Edge[phraseHashMap.size()];
		int index = 0;
		Set<String> phraseKeys = phraseHashMap.keySet();
		for (String key : phraseKeys) {
			String[] splitPhrase = key.split(" ");
			int src = (int) wordsHashMap.get(splitPhrase[0]);
			int dest = (int) wordsHashMap.get(splitPhrase[1]);
			edges[index] = new Edge(src, dest, (int) phraseHashMap.get(key));
			index++;
		}

		return new DirectedGraph(filteredWords, edges);
	}

	/*
	 * ��������ͼ���� JPG ͼƬ ͨ����������ͼ�ڽӱ������� dot �ű����ٵ��� dot ���������� JPG ͼƬ��
	 */
	public static void outputGraph(DirectedGraph graph) {
		String dot = "digraph G {\n";
		for (DirectedGraph.VertexNode vNode : graph.vexList) {
			DirectedGraph.EdgeNode eNode = vNode.m_firstEdge;
			String from = vNode.m_data + "->";
			while (eNode != null) {
				String color = "";
				if (eNode.m_visited)
					color = ", color=red";
				dot += "\t" + from + graph.vexList[eNode.m_vertex].m_data + " [label=" + eNode.m_weight + color
						+ "];\n";
				eNode = eNode.m_next;
			}
		}
		dot += "}";

		try {
			PrintWriter output = new PrintWriter(Configuration.DotScriptPath);
			output.write(dot, 0, dot.length());
			output.close();
			Process proc = Runtime.getRuntime().exec(new String[] { Configuration.GraphvizDotPath,
					Configuration.DotScriptPath, "-Tjpg", "-o", Configuration.JpgImagePath });
			proc.waitFor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		graph.resetVisited();
	}

	/*
	 * ��ѯ�ŽӴ� ���ݲ��� word1 �� word2����ѯ graph ���Ƿ�������������� �����ڣ����� graph �д��� word1->word3,
	 * word3->word2 �������ߣ��� word3 Ϊ�ŽӴʣ�������ӵ� ArrayList �� ���򷵻ؿ� Array
	 */
	public static ArrayList<String> queryBridgeWords(DirectedGraph graph, String word1, String word2) {
		int indexOfWord1 = graph.getIndexOfWord(word1);
		int indexOfWord2 = graph.getIndexOfWord(word2);

		ArrayList<String> bridgeWords = new ArrayList<String>();

		if (indexOfWord1 == -1 || indexOfWord2 == -1)
			return bridgeWords;

		DirectedGraph.EdgeNode nodeNextToWord1 = graph.vexList[indexOfWord1].m_firstEdge;
		while (nodeNextToWord1 != null) {
			DirectedGraph.EdgeNode eNode = graph.vexList[nodeNextToWord1.m_vertex].m_firstEdge;
			while (eNode != null) {
				String adjWord = graph.vexList[eNode.m_vertex].m_data;
				if (adjWord.equals(word2)) {
					String bridgeWord = graph.vexList[nodeNextToWord1.m_vertex].m_data;
					bridgeWords.add(bridgeWord);
				}
				eNode = eNode.m_next;
			}
			nodeNextToWord1 = nodeNextToWord1.m_next;
		}

		return bridgeWords;
	}

	/*
	 * �����ŽӴ��������ı� ����֮ǰ���ɵ�����ͼ�����㴫����ı����������ڵĵ��ʵ��ŽӴʣ����ŽӴʲ��뵽���������ڵĵ���֮�䡣
	 */
	public static String generateNewText(DirectedGraph graph, String inputText) {
		String[] words = inputText.split("[^a-zA-Z]+");
		String newText = "";
		int index = 0;

		if (words.length == 0)
			return inputText;

		for (int i = 0; i < words.length - 1; i++) {
			ArrayList<String> bridgeWords = queryBridgeWords(graph, words[i], words[i + 1]);
			String bridgeWord = "";
			if (bridgeWords.size() != 0) {
				Random r = new Random();
				index = r.nextInt(bridgeWords.size());
				bridgeWord = bridgeWords.get(index) + " ";
			}

			newText += words[i] + " " + bridgeWord;
		}
		newText += words[words.length - 1];

		return newText;
	}

	/*
	 * �������������ľ��� �����������֮����ڱߣ��򷵻رߵ�Ȩֵ�����򷵻������
	 */
	public static int calcDist(DirectedGraph graph, int src, int dest) {
		DirectedGraph.EdgeNode nodeNextToWord1 = graph.vexList[src].m_firstEdge;
		while (nodeNextToWord1 != null) {
			if (dest == nodeNextToWord1.m_vertex)
				return nodeNextToWord1.m_weight;
			else
				nodeNextToWord1 = nodeNextToWord1.m_next;
		}
		return Integer.MAX_VALUE;
	}

	/*
	 * �������·����prev���� ����������ѯ�ĵ�����ȷ����ͼ�г���
	 */
	public static int[] calcPrevArray(DirectedGraph graph, String word1) {
		final int MAXINT = Integer.MAX_VALUE;
		int vertNum = graph.vexList.length;
		int[] dist = new int[vertNum];
		int[] prev = new int[vertNum];
		boolean[] S = new boolean[vertNum];
		int src = graph.getIndexOfWord(word1);

		S[src] = true;

		// ��ʼ����ǰ��̾���
		for (int i = 0; i < vertNum; i++)
			dist[i] = calcDist(graph, src, i);

		// ��ʼ�����·��ǰ������
		for (int i = 0; i < vertNum; i++) {
			if (dist[i] == MAXINT)
				prev[i] = -1;
			else
				prev[i] = src;
		}

		for (int i = 0; i < vertNum; i++) {
			if (i == src)
				continue;

			int minDist = MAXINT;
			int v = src;

			// ��δ��ǵĶ����У��ҵ�����Դ������Ķ��㣬�����б��
			for (int j = 0; j < vertNum; j++) {
				if (!S[j] && dist[j] < minDist) {
					v = j;
					minDist = dist[j];
				}
			}
			S[v] = true;

			// �Ըձ����Ķ���Ϊ���ģ��������ڽӵ���Դ�㵱ǰ����̾���
			for (int j = 0; j < vertNum; j++) {
				int tempDist = calcDist(graph, v, j);
				if (!S[j] && tempDist < MAXINT) {
					if (dist[v] + tempDist < dist[j]) {
						dist[j] = dist[v] + tempDist;
						prev[j] = v;
					}
				}

				// �жϵ�ǰ�����Ƿ���ڵ���Դ��ıߣ�������������Ҫ�������Դ�㵽������� dist �� prev
				if (j == src && calcDist(graph, v, src) < MAXINT && dist[v] + tempDist < dist[src]) {
					dist[src] = dist[v] + tempDist;
					prev[src] = v;
				}
			}
		}

		return prev;
	}

	/*
	 * ������������֮������·�� ���� Dijkstra �㷨����Ǹ���Ȩ����ͼ�����·����������Դ�����·���ص���������
	 */
	public static String calcShortestPath(DirectedGraph graph, String word1, String word2) {
		int src = graph.getIndexOfWord(word1);
		int dest = graph.getIndexOfWord(word2);

		if (src == -1 || dest == -1)
			return "No path from " + word1 + " to " + word2 + ".";

		int[] prev = calcPrevArray(graph, word1);

		// ������ word1 �� word2 ��·�� ��������������������·���������
		if ((dest == src && prev[src] == -1) || prev[dest] == -1)
			return "No path from " + word1 + " to " + word2 + ".";

		// ����word1��Word2�����·��
		String result = "";
		int length = 0;
		int curIndex = dest;

		// �γ����·���ַ��������㳤��
		do {
			// �����·����Ӧ���ڽӱ�ı���Ϊ�ѷ���
			DirectedGraph.EdgeNode eNode = graph.vexList[prev[curIndex]].m_firstEdge;
			while (eNode.m_vertex != curIndex) {
				eNode = eNode.m_next;
			}
			eNode.m_visited = true;
			length += eNode.m_weight;

			result = "->" + graph.vexList[curIndex].m_data + result;
			curIndex = prev[curIndex];
		} while (curIndex != src);
		result = graph.vexList[src].m_data + result;
		result = "The length of shortest path is: " + String.valueOf(length) + "\n" + result;

		return result;
	}

	/*
	 * ����Դ�㵽�����ɴ�ڵ�����·��������
	 */
	public static String calcShortestPath(DirectedGraph graph, String word) {
		int src = graph.getIndexOfWord(word);
		if (src == -1)
			return "No " + word + " in the graph!";

		int[] prev = calcPrevArray(graph, word);
		String result = "";

		for (int dest = 0; dest < graph.vexList.length; dest++) {
			if ((dest == src && prev[src] == -1) || prev[dest] == -1)
				continue;

			String path = "";
			int curIndex = dest;

			// �γ����·���ַ���
			do {
				path = "->" + graph.vexList[curIndex].m_data + path;
				curIndex = prev[curIndex];
			} while (curIndex != src);
			path = graph.vexList[src].m_data + path;
			result += path + "\n";
		}

		return result.trim();
	}

	/*
	 * ������� ����Ĵ�ͼ��ѡ��һ���ڵ㣬�Դ�Ϊ����س��߽��������������¼���������нڵ�ͱߣ� ֱ�����ֵ�һ���ظ��ı�Ϊֹ�����߽����ĳ���ڵ㲻���ڳ���Ϊֹ��
	 * ���ñ��������нڵ��������ı������ء�
	 */
	public static String randomWalk(DirectedGraph graph) {
		String result = "";
		Random r = new Random();
		int vertIndex = r.nextInt(graph.vexList.length);

		DirectedGraph.VertexNode vNode = graph.vexList[vertIndex];
		result += vNode.m_data;
		while (vNode.m_count != 0) {
			DirectedGraph.EdgeNode eNode = vNode.m_firstEdge;
			int edgeIndex = r.nextInt(vNode.m_count + 1);
			for (int i = 1; i < edgeIndex; i++) {
				eNode = eNode.m_next;
			}
			vertIndex = eNode.m_vertex;
			result += " " + graph.vexList[vertIndex].m_data;
			if (eNode.m_visited)
				break;
			else
				eNode.m_visited = true;
			vNode = graph.vexList[vertIndex];
		}

		graph.resetVisited();
		return result;
	}
}
