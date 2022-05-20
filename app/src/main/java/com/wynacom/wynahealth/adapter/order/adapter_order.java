package com.wynacom.wynahealth.adapter.order;

public class adapter_order {
    String id               = null;
    String qty              = null;
    String subtotal         = null;
    String image            = null;
    String title            = null;
    String slug             = null;
    String description      = null;
    String product_price    = null;
    String view_discount    = null;
    String nom_discount     = null;
    String product_id       = null;

    public adapter_order(String id, String qty, String subtotal, String image, String title, String slug, String description, String product_price,String view_discount, String nom_discount,String product_id) {
        super();
        this.id             = id;
        this.qty            = qty;
        this.subtotal       = subtotal;
        this.image          = image;
        this.title          = title;
        this.slug           = slug;
        this.description    = description;
        this.product_price  = product_price;
        this.view_discount  = view_discount;
        this.nom_discount   = nom_discount;
        this.product_id     = product_id;
    }

    public String getID() {
        return id;
    }
    public void setID(String id) {
        this.id = id;
    }

    public String getQty() {
        return qty;
    }
    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getSubtotal() {
        return subtotal;
    }
    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }
    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getProduct_price() {
        return product_price;
    }
    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getNomDiscount() {
        return nom_discount;
    }
    public void setNomDiscount(String discount) {
        this.nom_discount = discount;
    }

    public String getView_discount() {
        return view_discount;
    }
    public void setView_discount(String view_discount) {
        this.view_discount = view_discount;
    }

    public String getProduct_id() {
        return product_id;
    }
    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }
}//String id, String qty, String subtotal, String image, String title, String slug, String description, String product_price, String discount
