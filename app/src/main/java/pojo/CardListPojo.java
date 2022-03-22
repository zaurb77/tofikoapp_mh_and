package pojo;

import java.util.ArrayList;

public class CardListPojo {

    public int status;
    public String message;
    public ArrayList<Responsedata> responsedata = new ArrayList<>();

    public class Responsedata {
        public String card_id;
        public String card_number;
        public String card_type;
        public int  selectedCard;
    }
}
