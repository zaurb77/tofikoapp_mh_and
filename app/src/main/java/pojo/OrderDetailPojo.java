package pojo;

import java.util.ArrayList;

public class OrderDetailPojo {


    public int status;
    public String message;
    public  ResponseData responsedata;

    public class ResponseData{


        public ArrayList<Items> items = new ArrayList<Items>();
        public String order_total;
        public String sub_total;
        public String address_line;
        public String delivery_type;
        public String delivery_note;
        public String delivery_time;
        public String payment_type;
        public String order_status;
        public String res_name;
        public String res_address;
        public String res_number;
        public String res_latitude;
        public String res_longitude;
        public String delivery_charge;
        public String is_cutlery;
        public String is_invoice;
        public String mangal_all_item_total;
        public String is_mangals;

        public class Items{

            public int cart_item_id;
            public String item_id;
            public int quantity;
            public String item_name;
            public String item_image;
            public int image_enable;
            public int is_offered;
            public String category;
            public String paid_customization;
            public String add_ons_cust_price;
            public String free_customization;
            public String taste_customization;
            public String cooking_customization;
            public String price;
            public String offered_price;

        }
    }
}
