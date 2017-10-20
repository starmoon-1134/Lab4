
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Text2Graph {
  private DirectedGraph graph;

  /**
   * javadocע������
   * 
   * @since 1.0
   * @version 1.1
   * @author xxx
   * @throws UnsupportedEncodingException
   */
  public void init() throws FileNotFoundException,
      UnsupportedEncodingException {
    InputStream inputStreamsss = new FileInputStream(
        new File(Configuration.TextFilePath));
    InputStreamReader buf = new InputStreamReader(
        inputStreamsss, "utf-8");
    Scanner input = new Scanner(buf);

    String text = new String("");
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

  public String generateNewText(String inputText) {
    return generateNewText(graph, inputText);
  }

  /*
   * �����ŽӴ��������ı� ����֮ǰ���ɵ�����ͼ�����㴫����ı����������ڵĵ��ʵ��ŽӴʣ����ŽӴʲ��뵽���������ڵĵ���֮�䡣
   */
  /**
   * javadocע������
   * 
   * @since 1.0
   * @version 1.1
   * @author xxx
   */
  public static String generateNewText(DirectedGraph graph,
      String inputText) {
    String[] words = inputText.split("[^a-zA-Z]+");
    String newText = "";
    int index = 0;

    if (words.length == 0) {
      return inputText;
    }

    for (int i = 0; i < words.length - 1; i++) {
      ArrayList<String> bridgeWords = queryBridgeWords(
          graph, words[i], words[i + 1]);
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

  public String randomWalk() {
    return randomWalk(graph);
  }

  /*
   * ������� ����Ĵ�ͼ��ѡ��һ���ڵ㣬�Դ�Ϊ����س��߽��������������¼���������нڵ�ͱ�
   * ֱ�����ֵ�һ���ظ��ı�Ϊֹ�����߽����ĳ���ڵ㲻���ڳ���Ϊֹ�����ñ��������нڵ��������ı������ء�
   */
  /**
   * javadocע������
   * 
   * @since 1.0
   * @version 1.1
   * @author xxx
   */
  public static String randomWalk(DirectedGraph graph) {
    String result = "";
    Random r = new Random();
    int vertIndex = r.nextInt(graph.vexList.length);

    DirectedGraph.VertexNode vvNode = graph.vexList[vertIndex];
    result += vvNode.myData;
    while (vvNode.myCount != 0) {
      DirectedGraph.EdgeNode eeNode = vvNode.myFirstEdge;
      int edgeIndex = r.nextInt(vvNode.myCount + 1);
      for (int i = 1; i < edgeIndex; i++) {
        eeNode = eeNode.myNext;
      }
      vertIndex = eeNode.myVertex;
      result += " " + graph.vexList[vertIndex].myData;
      if (eeNode.myVisited) {
        break;
      } else {
        eeNode.myVisited = true;
      }
      vvNode = graph.vexList[vertIndex];
    }

    graph.resetVisited();
    return result;
  }

  /*
   * ����Ӣ���ı����ж�Ӧ�б���������ͼ ������ Ӣ���ı��Կո�Ϊ�ָ���ָ��������ĵ����б� ԭ�� ʹ�� wordsHashMap
   * ȥ���ظ����ʺ�����ַ������� filteredWords �У���Ϊ�������ݼ��ϣ����ֵ�����ԭ�ı��г��ֵ��Ⱥ����
   * �Ե���Ϊ�����Ե�����filteredWords �е��±�Ϊֵ�����wordsHashMap���Ա����ɱ�ʱʹ�á�
   * ��ÿ�������ڵ�������Ϊ���飬�Ըô���Ϊ�����Ըô������ı��г��ֵĴ���Ϊֵ������ phraseHashMap�� ����
   * phraseHashMap���Դ�����׸������ڶ��㼯���е��±���Ϊ�ߵ� src���Եڶ������ʵ��±���Ϊ�ߵ� dest
   * �Դ�����ֵĴ�����Ϊ�ߵ�weight�������߼��ϡ� ���ö��㼯���Լ��߼��Ͻ�������ͼ�����ء�
   */
  /**
   * javadocע������
   * 
   * @since 1.0
   * @version 1.1
   * @author xxx
   */
  public static DirectedGraph generateGraph(
      String[] words) {
    HashMap wordsHashMap = new HashMap();

    // ȥ���ظ����ʣ��Ե���Ϊ�������ʳ��ֵĴ���Ϊֵ������ HashMap ��
    int count = 0;
    for (String word : words) {
      if (!wordsHashMap.containsKey(word)) {
        wordsHashMap.put(word, count);
        count++;
      }
    }

    String[] filteredWords = new String[wordsHashMap
        .size()];

    // ���� HashMap �ļ��������ظ��ĵ��ʼ��ϣ��Ե��ʶ�Ӧ��ֵ��Ϊ�±꣬�����ʴ����µ��ַ���������
    Iterator iterator = wordsHashMap.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<String, Integer> entry = (Entry<String, Integer>) iterator
          .next();
      int index = entry.getValue();
      filteredWords[index] = entry.getKey();
    }

    // ��ÿ�������ڵ�������Ϊ���飬�Ըô���Ϊ�����Ըô������ı��г��ֵĴ���Ϊֵ������ phraseHashMap��
    HashMap phraseHashMap = new HashMap();
    for (int i = 0; i < words.length - 1; i++) {
      String phrase = words[i] + " " + words[i + 1];
      if (phraseHashMap.containsKey(phrase)) {
        int value = (int) phraseHashMap.get(phrase);
        phraseHashMap.put(phrase, value + 1);
      } else {
        phraseHashMap.put(phrase, 1);
      }
    }

    Edge[] edges = new Edge[phraseHashMap.size()];
    int index = 0;
    Set<String> phraseKeys = phraseHashMap.keySet();
    for (String key : phraseKeys) {
      String[] splitPhrase = key.split(" ");
      int src = (int) wordsHashMap.get(splitPhrase[0]);
      int dest = (int) wordsHashMap.get(splitPhrase[1]);
      edges[index] = new Edge(src, dest,
          (int) phraseHashMap.get(key));
      index++;
    }

    return new DirectedGraph(filteredWords, edges);
  }

  public void outputGraph() {
    outputGraph(graph);
  }

  /*
   * ��������ͼ���� JPG ͼƬ ͨ����������ͼ�ڽӱ������� dot �ű����ٵ��� dot ���������� JPG ͼƬ��
   */
  /**
   * javadocע������
   * 
   * @since 1.0
   * @version 1.1
   * @author xxx
   */
  public static void outputGraph(DirectedGraph graph) {
    String dot = "digraph G {\n";
    for (DirectedGraph.VertexNode vvNode : graph.vexList) {
      DirectedGraph.EdgeNode eeNode = vvNode.myFirstEdge;
      String from = vvNode.myData + "->";
      while (eeNode != null) {
        String color = "";
        if (eeNode.myVisited) {
          color = ", color=red";
        }
        dot += "\t" + from
            + graph.vexList[eeNode.myVertex].myData
            + " [label=" + eeNode.myWeight + color + "];\n";
        eeNode = eeNode.myNext;
      }
    }
    dot += "}";

    try {
      PrintWriter output = new PrintWriter(
          Configuration.DotScriptPath);
      output.write(dot, 0, dot.length());
      output.close();
      Process proc = Runtime.getRuntime().exec(
          new String[] { Configuration.GraphvizDotPath,
              Configuration.DotScriptPath, "-Tjpg", "-o",
              Configuration.JpgImagePath });
      proc.waitFor();
    } catch (IOException e) {
      // TODO �Զ����ɵ� catch ��
      e.printStackTrace();
    } catch (InterruptedException e) {
      // TODO �Զ����ɵ� catch ��
      e.printStackTrace();
    }

    graph.resetVisited();
  }

  /**
   * javadocע������
   * 
   * @since 1.0
   * @version 1.1
   * @author xxx
   */
  @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
  public String queryBridgeWords(String word1,
      String word2) {
    ArrayList<String> bridgeWords = queryBridgeWords(graph,
        word1, word2);
    String result = "";
    final String toString = " to ";

    if (bridgeWords.size() == 0) {
      int indexOfWord1 = graph.getIndexOfWord(word1);
      int indexOfWord2 = graph.getIndexOfWord(word2);
      if (indexOfWord1 == -1 || indexOfWord2 == -1) {
        result = "No " + word1 + " or " + word2
            + " in the graph!";
      } else {
        result = "No bridge words from " + word1 + toString
            + word2 + "!";
      }
      return result;
    }

    result = "The bridge words from " + word1 + toString
        + word2 + " are:";
    int count = 1;
    int size = bridgeWords.size();
    for (String word : bridgeWords) {

      if (count == 1) {
        result += word;
      } else if (count == size) {
        result += " and " + word + ".";
      } else {
        result += ", " + word;
      }
      count++;
    }

    return result;
  }

  /*
   * ��ѯ�ŽӴ� ���ݲ��� word1�� word2����ѯ graph���Ƿ�������������� �����ڡ� ���� graph�д��� word1->word3
   * word3->word2 �������ߣ��� word3 Ϊ�ŽӴʣ�������ӵ� ArrayList �� ���򷵻ؿ� Array
   */
  /**
   * javadocע������
   * 
   * @since 1.0
   * @version 1.1
   * @author xxx
   */
  public static ArrayList<String> queryBridgeWords(
      DirectedGraph graph, String word1, String word2) {
    int indexOfWord1 = graph.getIndexOfWord(word1);
    int indexOfWord2 = graph.getIndexOfWord(word2);

    ArrayList<String> bridgeWords = new ArrayList<String>();

    if (indexOfWord1 == -1 || indexOfWord2 == -1) {
      return bridgeWords;
    }

    DirectedGraph.EdgeNode nodeNextToWord1 = graph.vexList[indexOfWord1].myFirstEdge;
    while (nodeNextToWord1 != null) {
      DirectedGraph.EdgeNode eeNode = graph.vexList[nodeNextToWord1.myVertex].myFirstEdge;
      while (eeNode != null) {
        String adjWord = graph.vexList[eeNode.myVertex].myData;
        if (adjWord.equals(word2)) {
          String bridgeWord = graph.vexList[nodeNextToWord1.myVertex].myData;
          bridgeWords.add(bridgeWord);
        }
        eeNode = eeNode.myNext;
      }
      nodeNextToWord1 = nodeNextToWord1.myNext;
    }

    return bridgeWords;
  }

  /*
   * �������������ľ��� �����������֮����ڱߣ��򷵻رߵ�Ȩֵ�����򷵻������
   */
  /**
   * javadocע������
   * 
   * @since 1.0
   * @version 1.1
   * @author xxx
   */
  public static int calcDist(DirectedGraph graph, int src,
      int dest) {
    DirectedGraph.EdgeNode nodeNextToWord1 = graph.vexList[src].myFirstEdge;
    while (nodeNextToWord1 != null) {
      if (dest == nodeNextToWord1.myVertex) {
        return nodeNextToWord1.myWeight;
      } else {
        nodeNextToWord1 = nodeNextToWord1.myNext;
      }
    }
    return Integer.MAX_VALUE;
  }

  /*
   * �������·����prev���� ����������ѯ�ĵ�����ȷ����ͼ�г���
   */
  /**
   * javadocע������
   * 
   * @since 1.0
   * @version 1.1
   * @author xxx
   */
  public static int[] calcPrevArray(DirectedGraph graph,
      String word1) {
    final int maxInt = Integer.MAX_VALUE;
    int vertNum = graph.vexList.length;
    int[] dist = new int[vertNum];
    int[] prev = new int[vertNum];
    boolean[] ssS = new boolean[vertNum];
    int src = graph.getIndexOfWord(word1);

    ssS[src] = true;

    // ��ʼ����ǰ��̾���
    for (int i = 0; i < vertNum; i++) {
      dist[i] = calcDist(graph, src, i);
    }

    // ��ʼ�����·��ǰ������
    for (int i = 0; i < vertNum; i++) {
      if (dist[i] == maxInt) {
        prev[i] = -1;
      } else {
        prev[i] = src;
      }
    }

    for (int i = 0; i < vertNum; i++) {
      if (i == src) {
        continue;
      }

      int minDist = maxInt;
      int v = src;

      // ��δ��ǵĶ����У��ҵ�����Դ������Ķ��㣬�����б��
      for (int j = 0; j < vertNum; j++) {
        if (!ssS[j] && dist[j] < minDist) {
          v = j;
          minDist = dist[j];
        }
      }
      ssS[v] = true;

      // �Ըձ����Ķ���Ϊ���ģ��������ڽӵ���Դ�㵱ǰ����̾���
      for (int j = 0; j < vertNum; j++) {
        int tempDist = calcDist(graph, v, j);
        if (!ssS[j] && tempDist < maxInt) {
          if (dist[v] + tempDist < dist[j]) {
            dist[j] = dist[v] + tempDist;
            prev[j] = v;
          }
        }

        // �жϵ�ǰ�����Ƿ���ڵ���Դ��ıߣ�������������Ҫ�������Դ�㵽������� dist �� prev
        if (j == src && calcDist(graph, v, src) < maxInt
            && dist[v] + tempDist < dist[src]) {
          dist[src] = dist[v] + tempDist;
          prev[src] = v;
        }
      }
    }

    return prev;
  }

  /**
   * javadocע������
   * 
   * @since 1.0
   * @version 1.1
   * @author xxx
   */
  public String calcShortestPath(String word1,
      String word2) {
    if (word2.equals("")) {
      return calcShortestPath(graph, word1);
    } else {
      return calcShortestPath(graph, word1, word2);
    }
  }

  /*
   * ������������֮������·�� ���� Dijkstra �㷨����Ǹ���Ȩ����ͼ�����·����������Դ�����·���ص���������
   */
  /**
   * javadocע������
   * 
   * @since 1.0
   * @version 1.1
   * @author xxx
   */
  public static String calcShortestPath(DirectedGraph graph,
      String word1, String word2) {
    int src = graph.getIndexOfWord(word1);
    int dest = graph.getIndexOfWord(word2);

    if (src == -1 || dest == -1) {
      return "No path from " + word1 + " to " + word2 + ".";
    }

    int[] prev = calcPrevArray(graph, word1);

    // ������ word1 �� word2 ��·�� ��������������������·���������
    if ((dest == src && prev[src] == -1)
        || prev[dest] == -1) {
      return "No path from " + word1 + " to " + word2 + ".";
    }

    // ����word1��Word2�����·��
    String result = "";
    int length = 0;
    int curIndex = dest;

    // �γ����·���ַ��������㳤��
    do {
      // �����·����Ӧ���ڽӱ�ı���Ϊ�ѷ���
      DirectedGraph.EdgeNode eeNode = graph.vexList[prev[curIndex]].myFirstEdge;
      while (eeNode.myVertex != curIndex) {
        eeNode = eeNode.myNext;
      }
      eeNode.myVisited = true;
      length += eeNode.myWeight;

      result = "->" + graph.vexList[curIndex].myData
          + result;
      curIndex = prev[curIndex];
    } while (curIndex != src);
    result = graph.vexList[src].myData + result;
    result = "The length of shortest path is: "
        + String.valueOf(length) + "\n" + result;

    return result;
  }

  /*
   * ����Դ�㵽�����ɴ�ڵ�����·��������
   */
  /**
   * javadocע������
   * 
   * @since 1.0
   * @version 1.1
   * @author xxx
   */
  public static String calcShortestPath(DirectedGraph graph,
      String word) {
    int src = graph.getIndexOfWord(word);
    if (src == -1) {
      return "No " + word + " in the graph!";
    }

    int[] prev = calcPrevArray(graph, word);
    String result = "";

    for (int dest = 0; dest < graph.vexList.length; dest++) {
      if ((dest == src && prev[src] == -1)
          || prev[dest] == -1) {
        continue;
      }

      String path = "";
      int curIndex = dest;

      // �γ����·���ַ���
      do {
        path = "->" + graph.vexList[curIndex].myData + path;
        curIndex = prev[curIndex];
      } while (curIndex != src);
      path = graph.vexList[src].myData + path;
      result += (path + "\n");
    }
    return result.trim();
  }

}
