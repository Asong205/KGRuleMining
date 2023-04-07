package com.mining.entity;

import java.util.ArrayList;
import java.util.List;

public class RuleEntity {
    private String head;
    private String body;
    //amie
    private String HeadCoverage;
    private String StdConf;
    private String PCAConf;
    private String PositiveEp;



    //anyburl
    private String BodySize2;
    private String SupDegree2;
    private String StdConf2;

    public String getHead() {
        return head;
    }

    public RuleEntity(String body, String head, String headCoverage, String stdConf, String PCAConf, String positiveEp) {
        this.head = head;
        this.body = body;
        HeadCoverage = headCoverage;
        StdConf = stdConf;
        this.PCAConf = PCAConf;
        PositiveEp = positiveEp;
    }
    public RuleEntity(String head, String body, String BodySize, String supDegree2, String stdConf2) {
        this.head = head;
        this.body = body;
        this.BodySize2 = BodySize;
        SupDegree2 = supDegree2;
        StdConf2 = stdConf2;
    }





    public List<String> getList(){
        List<String> list=new ArrayList<>();
        list.add(body);
        list.add("  =>  ");
        list.add(head);
        list.add(HeadCoverage);
        list.add(StdConf);
        list.add(PCAConf);
        list.add(PositiveEp);
        return list;

    };

    public List<String> getList2(){
        List<String> list=new ArrayList<>();
        list.add(body);
        list.add("  =>  ");
        list.add(head);
        list.add(BodySize2);
        list.add(SupDegree2);
        list.add(StdConf2);

        return list;

    };

    public List<String> getrule(){
        List<String> list=new ArrayList<>();
        list.add(body);
        list.add("  =>  ");
        list.add(head);

        return list;

    };
}
