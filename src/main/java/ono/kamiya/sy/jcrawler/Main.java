package ono.kamiya.sy.jcrawler;

import ono.kamiya.sy.jcrawler.analyzer.MyGalgameAnalyzer;

public class Main {
    public static void main(String[] args) {
        MyGalgameAnalyzer analyzer = new MyGalgameAnalyzer();
        Object result = analyzer.start();
        System.out.println(result);
    }
}
