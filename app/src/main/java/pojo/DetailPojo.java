package pojo;

import java.util.ArrayList;

public class DetailPojo {

    public int status;
    public String message;
    public String res_open_error;
    public String pre_order_accept;
    public Responsedata responsedata;

    public class Responsedata {
        public String item_id;
        public String item_name;
        public String price;
        public String item_image;
        public String image_enable;
        public String is_offered;
        public String category;
        public String ingredients;
        public String allergens;
        public String is_favourite;
        public String in_cart;
        public String cart_item_id;
        public String quantity;
        public String barcode;
        public int qty;
        public String is_taste;
        public String isbn;
        public String description;
        public String url;
        public String manufacturer;
        public String publisher;
        public String prev_customization;
        public String offered_price;
        public String mangals;
        public String size;
        public int is_show;

        public ArrayList<FreeCustomization> remove_customization = new ArrayList<>();
        public ArrayList<PaidCustomization> customization = new ArrayList<>();
        public ArrayList<CookingGrades> cooking_grades = new ArrayList<>();
        public ArrayList<CartItem> cart_items = new ArrayList<>();
        public ArrayList<String> images = new ArrayList<>();
        public ArrayList<Taste> taste = new ArrayList<>();

        public class FreeCustomization {
            public int id;
            public String name;
            public String price;
            public boolean freeSelected;
        }

        public class PaidCustomization {
            public int id;
            public String name;
            public String price;
            public boolean paidSelected;
        }

        public class CookingGrades {

            public int id;
            public String name;
            public String price;
            public int is_default;
        }

        public class Taste {
            public int id;
            public String name;
            public String price;
            public int is_default;
        }

        public class CartItem {
            public int cart_item_id;
            public String quantity;
        }
    }
}
