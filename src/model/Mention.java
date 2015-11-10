package model;


import java.util.HashMap;
import java.util.Map;

public class Mention {
    private boolean textMatchMode = false;

    private String text;
    private int startOffset, endOffset;

    private String annotator;
    private String id;
    private String doc;
    private String translation;
    private String type;
    private String wiki;

    private Map<String, Object> attributeMap = new HashMap<String, Object>();

    /** CONSTRUCTORS */
    public Mention() {

    }

    public Mention(String text, int startOffset, int endOffset) {
        this.text         = text;
        this.startOffset = startOffset;
        this.endOffset   = endOffset;
    }

    /** TEXT */
    public void text(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }

    /** OFFSET */
    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    public void setEndOffset(int endOffset) {
        this.endOffset = endOffset;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public int getEndOffset() {
        return endOffset;
    }

    /** OTHER ATTRIBUTES */
    public void setAnnotator(String annotator) {
        this.annotator = annotator;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setWiki(String wiki) {
        this.wiki = wiki;
    }

    public String getAnnotator() {
        return annotator;
    }

    public String getId() {
        return id;
    }

    public String getDoc() {
        return doc;
    }

    public String getTranslation() {
        return translation;
    }

    public String getType() {
        return type;
    }

    public String getWiki() {
        return wiki;
    }

    public void setTextMatchMode(boolean textMatchMode) {
        this.textMatchMode = textMatchMode;
    }

    public void textMatchMode() {
        textMatchMode = true;
    }

    /** ATTRIBUTES MAP */
    public boolean hasAttribute(String key) {
        return attributeMap.containsKey(key);
    }

    public Object getAttribute(String key) {
        return attributeMap.get(key);
    }

    public void putAttribute(String key, Object value) {
        attributeMap.put(key, value);
    }

    public boolean removeAttribute(String key) {
        if (attributeMap.containsKey(key)) {
            attributeMap.remove(key);
            return true;
        } else {
            return false;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (o.getClass().equals(this.getClass())) {
            String doc = ((Mention) o).getDoc();
            if (textMatchMode) {
                String text = ((Mention) o).text();
                return text.equals(this.text) && doc.equals(this.doc);
            } else {
                int startOffset = ((Mention) o).getStartOffset();
                int endOffset = ((Mention) o).getEndOffset();
                return startOffset == this.startOffset && endOffset == this.endOffset && doc.equals(this.doc);
            }
        } else {
            return false;
        }
    }

    public boolean match(Mention men) {
        if (textMatchMode) {
            return men.text().equals(this.text)
                    && men.getDoc().equals(this.doc)
                    && men.getType().equals(this.type)
                    && men.getWiki().equals(this.wiki)
                    && men.getTranslation().equals(this.translation);
        } else {
            return men.getStartOffset() == this.startOffset
                    && men.getEndOffset() == this.endOffset
                    && men.getDoc().equals(this.doc)
                    && men.getType().equals(this.type)
                    && men.getWiki().equals(this.wiki)
                    && men.getTranslation().equals(this.translation);
        }
    }
}
