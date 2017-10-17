1.如果text.txt第一个单词之前包含非英文字符，text.split()之后words[0]是空串，并拿去构建图，最终导致调用dot.exe失败。
	                     ----------Text2Graph.java新增9、10，26~34行

2.实验要求是不区分大小写的，从Show Graph窗口的图片来看There和there会被B被当做2个单词处理。后续的Bridge Words, New Text, Shortest Path均受到影响
                        --------TextGraph.java 新增36行
                        --------MainFrame.java 修改112、116、124行

