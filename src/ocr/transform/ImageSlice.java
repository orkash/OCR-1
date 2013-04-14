package ocr.transform;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

public class ImageSlice {
    
    private static final int THROTTLE = 200;

    private BufferedImage image;
    private byte[] pixels = null;

    public ImageSlice(BufferedImage image) {
        this.image = image;
    }

    public List<List<Integer>> sliceColumns(boolean[] rows) {
        byte[] pixels = getPixels();
        int width = image.getWidth();
        
        // x = 0; y1 = 1; y2 = 2
        List<List<Integer>> values = new ArrayList<List<Integer>>();
        values.add(new ArrayList<Integer>());
        values.add(new ArrayList<Integer>());
        values.add(new ArrayList<Integer>());

        for (int row = 0; row < rows.length; row++) {
            if (!rows[row]) {
                continue;
            }
            
            int count = 0;
            while (rows[row + count]) {
                count++;
            }

            for (int w = 0; w < width; w++) {
                for (int c = 0; c <= count; c++) {
                    int p = 3 * (w + (row + c) * width);
                    
                    int b = pixels[p] & 0xff;
                    int g = pixels[p + 1] & 0xff;
                    int r = pixels[p + 2] & 0xff;
                    
                    if (r <= THROTTLE && g <= THROTTLE && b <= THROTTLE) {
                        values.get(0).add(w);
                        values.get(1).add(row);
                        values.get(2).add(row + count);
                        break;
                    }
                }
            }
            
            row += count;
        }
        
        return values;
    }

    public boolean[] sliceRows() {
        byte[] pixels = getPixels();

        int width = image.getWidth();
        int height = image.getHeight();

        boolean[] plate = new boolean[height];

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                int p = 3 * (w + h * width);

                int b = pixels[p] & 0xff;
                int g = pixels[p + 1] & 0xff;
                int r = pixels[p + 2] & 0xff;

                boolean black = r <= THROTTLE && g <= THROTTLE && b <= THROTTLE;

                if (black) {
                    plate[h] = true;
                    break;
                }
            }
        }

        return plate;
    }

    private byte[] getPixels() {
        if (pixels == null) {
            pixels = ((DataBufferByte) image.getRaster().getDataBuffer())
                    .getData();
        }
        
        return pixels;
    }

}
