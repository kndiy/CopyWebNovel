import java.io.IOException;

public class MainRun {

    public static void main(String[] args) throws IOException {
        String outputName = "D:\\Books\\WN CN Nightfall\\WN CN Nightfall.docx";

        String urlHead = "https://boxnovel.com/novel/nightfall/chapter-";
        String finalChapUrl = "https://boxnovel.com/novel/nightfall/chapter-1118-end/";

        int start = 1;
        int end = 1118;

        CopyWebNovel copyOperation = new CopyWebNovel();
        copyOperation.copyWebNovel(outputName, start, end, urlHead, finalChapUrl);
    }
}
