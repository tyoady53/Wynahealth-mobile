package com.wynacom.wynahealth.adapter.product;

public class adapter_product {
    String id               = null;
    String title            = null;
    String ol_category_id   = null;
    String description      = null;
    String price            = null;
    String discount         = null;
    String slug             = null;
    String image            = null;

    boolean selected        = false;

    public adapter_product(String id, String title, String ol_category_id, String description, String price, String discount, String slug, String image, boolean selected) {
        super();
        this.id             = id;
        this.title          = title;
        this.ol_category_id = ol_category_id;
        this.description    = description;
        this.price          = price;
        this.discount       = discount;
        this.slug           = slug;
        this.image          = image;
        this.selected       = selected;
    }

    public String getID() {
        return id;
    }
    public void setID(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getCetegory() {
        return ol_category_id;
    }
    public void setCategory(String ol_category) {
        this.ol_category_id = ol_category;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }

    public String getSlug() {
        return slug;
    }
    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public String getDiscount() {
        return discount;
    }
    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
