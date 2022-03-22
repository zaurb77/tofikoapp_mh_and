package pojo;

import java.util.ArrayList;

public class OfferListPojo {

    public int status;
    public String message;

    public ResponseData responsedata;


    public class ResponseData{


        public ArrayList<OfferItems> offer_items = new ArrayList<>();
        public String qr_image;
        public String points;


        public class OfferItems{
            public String id;
            public String item_id;
            public String mangals;
            public String offered_price;
            public String original_price;
            public String is_offer_active;
            public String offer_name;
            public String image;
            public String restaurant_name;
            public String restaurant_id;
            public String res_open_error;
            public String is_order_accept;
        }
    }
}
