public class Article {

    String number ;
    String name ;
    String client;
    String okved ;
    String dataStart;
    String dataEnd ;
    String myUrl ;

    public Article(String number, String name, String client, String okved, String dataStart, String dataEnd, String myUrl) {
        this.number = number;
        this.name = name;
        this.client = client;
        this.okved = okved;
        this.dataStart = dataStart;
        this.dataEnd = dataEnd;
        this.myUrl = myUrl;
    }

    @Override
    public String toString() {
        return "Article{" +
                "number='" + number + '\'' +
                ", name='" + name + '\'' +
                ", client='" + client + '\'' +
                ", okved='" + okved + '\'' +
                ", dataStart='" + dataStart + '\'' +
                ", dataEnd='" + dataEnd + '\'' +
                ", myUrl='" + myUrl + '\'' +
                '}';
    }
}
