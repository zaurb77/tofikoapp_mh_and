package pojo;

import java.util.ArrayList;

public class CartListPojo {

    public int status;
    public String message;

    public RestaurantData responsedata;

    public class RestaurantData {

        public ArrayList<Items> items = new ArrayList<>();

        public class Items{
            public int cart_item_id;
            public int item_id;
            public int quantity;
            public String item_name;
            public String price;
            public String item_image;
            public String category;
            public String paid_customization;
            public String free_customization;
            public String taste_customization;
            public String cooking_customization;
            public int image_enable;
            public int is_offered;
            public String main_price;
            public String need_mangals;
            public String mangal_remain_price;

        }

        public String restaurant_name;
        public String restaurant_address;
        public String restaurant_phone;
        public String delivery_charge;
        public int res_is_open;
        public String next_open_time;
        public String order_total;
        public int is_mangals_cart;
        public String address;
        public String address_id;
        public String order_type;
        public String res_open_error;
        public String total_pay_amount;
        public String total_main_pay_amount;
        public String mangal_all_item_total;

    }
}
