package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //初始化根节点
    private TrieNode rootNode = new TrieNode();

    //替换字符
    private static final String REPLACE_CHARACTER = "***";

    //初始化
    @PostConstruct
    private void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                ) {
            String keyWord;
            while((keyWord = br.readLine()) != null){
                addKeyWord(keyWord);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败！" + e.getMessage());
        }
    }

    public String filter(String text) {
        //如果文本为空
        if(StringUtils.isBlank(text)) {
            return null;
        }

        //指针1
        TrieNode tempNode = rootNode;
        //指针2
        int begin = 0;
        //指针3
        int position = 0;
        //结果
        StringBuilder sb = new StringBuilder();

        while(begin < text.length()) {
            char c = text.charAt(position);
            //如果当前指针指向字符
            if(isSymbol(c)) {
                if (tempNode == rootNode) {
                    sb.append(c);
                    ++begin;
                }
                ++position;
                continue;
            }

            //检查下级节点
            tempNode = tempNode.getSubNode(c);
            if(tempNode == null) {
                //以begin开头的不是敏感词
                sb.append(text.charAt(begin));
                //进入下个节点
                position = ++begin;
                tempNode = rootNode;
            } else if(tempNode.isKeyEndWords) {
                sb.append(REPLACE_CHARACTER);
                begin = ++position;
                tempNode = rootNode;
            } else {
                ++position;
                //指针3走到头，没有敏感词，但仍可能存在敏感词片段，只将begin字符放入
                if(position >= text.length()) {
                    sb.append(text.charAt(begin));
                    position = ++begin;
                    tempNode = rootNode;
                }
            }
        }
        return sb.toString();
    }

    //判断是否为符号
    private boolean isSymbol(Character c) {
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //将敏感词挂到前缀树
    private void addKeyWord(String keyWord) {
        //临时节点，指针
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyWord.length(); i++) {
            char c = keyWord.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            //如果该节点没有子节点
            if(subNode == null) {
                //初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }
            //将临时节点指向子节点
            tempNode = subNode;

            //设置结束标记
            if(i == keyWord.length() - 1) {
                tempNode.setKeyEndWords(true);
            }
        }
    }

    //定义前缀树
    private class TrieNode {
        //是否为结束标识
        private boolean isKeyEndWords = false;

        //子节点 key为子字符 value为子节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeyEndWords() {
            return isKeyEndWords;
        }

        public void setKeyEndWords(boolean keyEndWords) {
            isKeyEndWords = keyEndWords;
        }

        //增加子节点
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }
}
