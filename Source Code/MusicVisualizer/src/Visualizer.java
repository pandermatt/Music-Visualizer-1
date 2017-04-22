import ddf.minim.AudioInput;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;
import processing.core.PApplet;
import processing.core.PVector;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Visualizer extends PApplet{
    public static Minim minim;
    public static AudioPlayer player;
//    public static AudioInput player;
    public static FFT fft;

    private static float smoothing = 0.75f;
    public  static float[] fftSmooth;
    public  static int avgSize;

    private float minVal = 0.0f, maxVal = 0.0f;
    private boolean firstMinDone = false;
    public  static boolean keyDown;
    private static boolean showText = true;
    private static boolean isPaused = false;

    private int skip = 5 * 1000;
    private float cAmplitude = 90;
    private float ellipseR = 0;
    private float starKick = 69.0f;
    private float r = 165;
    private static float strokeWeight = 2.4f;
    private static float widthstepincrement = .1f;
    private static int amplitude = 1;
    private static int barStep = 1;
    private int buffersize = 2048 / 2;
    private float angOffset = 3;
    public static float a = 0;
    public Star[] stars = new Star[265];

    public float speed = 1;
    // /media/caleb/OS/Users/Caleb/Downloads

    private int visualMode = 3;

    private static String iconImage = "logo3.png";
    public static Image icon = Toolkit.getDefaultToolkit().getImage(Visualizer.class.getResource("/" + iconImage));
    private String music = "Wildfire.mp3";

    public static String[] instructions = {
            "Z: Show/Hide Text :D",
            "1: Decrease Amplitude",
            "2: Increase Amplitude",
            "3: Decrease Smoothing",
            "4: Increase Smoothing",
            "5: Decrease Angle Offset",
            "6: Increase Angle Offset",
            "7: Decrease StarKick",
            "8: Increase StarKick",
            "W: Increase Radius",
            "S: Decrease Radius",
            "A/D: Skip 5 Seconds",
            "X: Change Visualizer",
            "V: Change Song (On Computer)",
            "Left: Decrease Bars",
            "Right: Increase Bars",
            "Up: Increase Stroke",
            "Down: Decrease Stroke",
            "Ctrl+Alt: Exception Log"
    };

    public static void main(String[] args) {PApplet.main(Visualizer.class);}

    public void setup() {
        surface.setResizable(true);
        surface.setFrameRate(120);
        surface.setIcon(loadImage(iconImage));
//        smooth();
        //strokeCap(SQUARE);
    }

    public void settings() {
        size(1105, 860, JAVA2D);
//        PJOGL.setIcon("enginelogo.png");
        minim = new Minim(this);
        setUpPlayer();
        setUpBackGround();
//        smooth(12);
    }

    private void setUpPlayer() {
        player = minim.loadFile(fileChooser(music), buffersize);
//        player = minim.getLineIn();
//        player = minim.loadFile((music), 2048);
        fft = new FFT(player.bufferSize(), player.sampleRate());
        fft.logAverages(2000, 200);
        avgSize = fft.avgSize();
        fftSmooth = new float[avgSize];
        player.play();
    }

    public void draw(){
        background(0);
        EException.update();
        fft.forward(player.mix);
        displayInstructions();
        showBackGround();
        showSongTime();
        cAmplitude = lerp(cAmplitude, ((smoothing) * fft.calcAvg(minVal, maxVal) * 1), .1f);
        ellipseR = constrain(cAmplitude * 3.7f, 0, (r * 3) - 50);
        speed = map(ellipseR, 0, r * 2 - 50, .1f, 6.2f);
        visualize();
        EException.setText(isSongOver()+"");
        //songEnded();
    }

    public boolean isSongOver(){
        return (!isPaused && (player.right.level() <= 0 && player.left.level() <= 0));
    }

    public void songEnded(){
        if (player != null)
            EException.setText(player.position() + " (" + player.left.level() + " - " + player.right.level() + ") " + player.isPlaying());
        /*if (!isPaused) {
        }*/
    }

    private void visualize(){
        switch (visualMode) {
            case 0: circulerPulse(); break;
            case 1: signal(); break;
            case 2: inverse(); break;
            case 3 : functionMusic(); break;
            default: break;
        }
    }

    private float dB(float x) {return (x == 0) ? 0 : 10 * (float) Math.log10(x);}

    private void smoothPulse(boolean useDB) {
        for (int i = 0; i < avgSize; i++) {
            // Get spectrum value (using dB conversion or not, as desired)
            float fftCurr;
            if (useDB) {fftCurr = dB(fft.getAvg(i));} else {fftCurr = fft.getAvg(i);}

            // Smooth using exponential moving average
            fftSmooth[i] = (smoothing) * fftSmooth[i] + ((1 - smoothing) * fftCurr);

            // Find max and min values ever displayed across whole spectrum
            if (fftSmooth[i] > maxVal) {maxVal = fftSmooth[i];}
            if (!firstMinDone || (fftSmooth[i] < minVal)) {minVal = fftSmooth[i];}
        }
    }

    private void circulerPulse() {
        smoothPulse(false);
        final float maxHeight = (height / 2) * 0.75f;

        // Calculate the total range of smoothed spectrum; this will be used to scale all values to range 0...1
        final float range = maxVal - minVal;
        final float scaleFactor = range + 0.00001f; // avoid div. by zero

        for (int i = 0; i < avgSize; i += barStep) {
            float angle = i * angOffset * (2 * PI) / avgSize;
            float fftSmoothDisplay = maxHeight * (((amplitude * fftSmooth[i]) - minVal) / scaleFactor);
            float x = (r * 2) * cos(angle);
            float y = (r * 2) * sin(angle);
            float x2 = ((r * 2) + fftSmoothDisplay) * cos(angle) + .85f;
            float y2 = ((r * 2) + fftSmoothDisplay) * sin(angle) + .85f;
//            int c = (int) map(i, 0, fft.specSize(), 0, 470);
            int c = (int) map(i, 0, fft.specSize(), 0, 285);
            colorMode(HSB);
            stroke(c, 255, 255);
            strokeWeight(strokeWeight);
            line(x + width / 2, y + height / 2, x2 + width / 2, y2 + height / 2);
        }

        noStroke();
        fill(lerp(0, map(ellipseR, 0, (r * 3) - 50, 0, 255), .23f), 255, 255);
        ellipse(width / 2, height / 2, ellipseR, ellipseR);
    }

    private void signal() {
        final float maxHeight = (height / 2) * 0.75f;
        smoothPulse(false);

        // Calculate the total range of smoothed spectrum; this will be used to scale all values to range 0...1
        final float range = maxVal - minVal;
        final float scaleFactor = range + 0.00001f; // avoid div. by zero

        for (int i = 0; i < avgSize - 1; i += barStep) {
            float x1 = map(i, 0, avgSize, 0, width);
            float x2 = map(i + 1, 0, avgSize, 0, width);
            float amp = maxHeight * (((amplitude * fftSmooth[i]) - minVal) / scaleFactor);
            float y = (height / 2 - 20);

            int c = (int) map(i, 0, fft.specSize(), 0, 285);
            stroke(c, 255, 255);
            strokeWeight(strokeWeight);
            line(x1, y + amp, x2, y - amp);
        }
    }

    private void inverse() {
        final float maxHeight = (height / 2) * 0.75f;
        smoothPulse(false);

        // Calculate the total range of smoothed spectrum; this will be used to scale all values to range 0...1
        final float range = maxVal - minVal;
        final float scaleFactor = range + 0.00001f; // avoid div. by zero

        int size = avgSize - 1;
        for (int i = 0; i < size; i += barStep) {
            float x1 = map(i, 0, size, 0, width);
            float x2 = map(i + 1, 0, size, 0, width);

            float x3 = map(i, 0, size, width, 0);
            float x4 = map(i + 1, 0, size, width, 0);

            float amp = maxHeight * (((amplitude * fftSmooth[i]) - minVal) / scaleFactor);
            float y = (height / 2 - 20);

            int c = (int) map(i, 0, fft.specSize(), 0, 285);
            stroke(c, 255, 255);
            strokeWeight(strokeWeight);
            //triangle(width/2-x1,height/4+amp,width/5-x2,y-amp,width*.7f,height*.7f-amp);
            line(x1, y, x2, y + amp);
            line(x3, y, x4, y - amp);
        }
    }

    public void functionMusic(){
        final float maxHeight = (height / 2) * 0.75f;
        smoothPulse(false);

        // Calculate the total range of smoothed spectrum; this will be used to scale all values to range 0...1
        final float range = maxVal - minVal;
        final float scaleFactor = range + 0.00001f; // avoid div. by zero

        for (int i = 0; i < avgSize - 1; i += barStep) {
            float amp = maxHeight * (((amplitude * fftSmooth[i]) - minVal) / scaleFactor);

            float x1 = map(i, 0, avgSize, 0, width);
            float x2 = map(i + 1, 0, avgSize, 0, width);
            float y1 = -musicTransform(x1) + height / 2;
            float y2 = -musicTransform(x2) + height / 2;

            int c1 = (int) map(i, 0, fft.specSize(), 0, 285);
            stroke(c1, 255, 255);
            strokeWeight(strokeWeight);
            line(1.7f * x1, y1 + amp, 1.7f * x2, y2 - amp);
        }
        a += 0.025f;
    }

    public float musicTransform(float x) {
        float m = 2, a = 325;
        return (float) ((m * (x - a) * Math.signum(a - x) + m * a) - height / 2 + (60 * Math.sin(.08f * x + Visualizer.a))); // Triangle Function: http://math.stackexchange.com/questions/544559/is-there-any-equation-for-triangle
//        return (float) (75f * Math.sin(.015 * x + a));
//        return 20f * sin(2 * sin(2 * sin(2 * sin(2 * sin(2 * sin(2 * sin(2 * sin(.04f * x))))))));
//        return (float) Math.pow(.02 * x, 2);
    }

    private void displayInstructions() {
        if (showText) {
            fill(255);
            textSize(14);
            text("Smoothing: " + (int) (smoothing * 100) + "   Amplitude: " + amplitude + "   Radius: " + r + "   BarStep: " + barStep
                    + "   StrokeWeight: " + String.format("%.2f", strokeWeight) + "   Angle Offset: " + String.format("%.2f", angOffset)
                    + "   StarKick: " + String.format("%.2f", starKick), 5, 32);

            int starty = 56, spacing = 18 /*Pixels*/;
            for (int i = 0; i < instructions.length; i++) {text(instructions[i], 5, starty); starty += spacing;}
        }
    }

    private void showSongTime() {
        try {
            surface.setTitle(String.format("%d X %d -- Song Time: %s", width, height, (new SimpleDateFormat("mm:ss")).format(new Date(player.position()))));
            float posx = map(player.position(), 0, player.length(), 0, width);
            noStroke();
            colorMode(HSB);
            fill(map(player.position(), 0, player.length(), 0, 255), 255, 255);
            rect(0, 0, posx, 12);
        } catch (Exception e){EException.append(e);}
    }

    private String fileChooser(String if_null) {
        try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch (Exception x){EException.append(x);}
        JFileChooser chooser = new JFileChooser(System.getProperty("user.home") + "/Downloads");
        JFrame window = new JFrame();
        window.setIconImage(icon);
        chooser.setDialogTitle("Choose a song :D");
        chooser.setFileFilter(new FileNameExtensionFilter(".wav, .mp3", "wav", "mp3"));
        int returnVal = chooser.showOpenDialog(window);
        if(returnVal == JFileChooser.APPROVE_OPTION) {return chooser.getSelectedFile().getAbsolutePath();
        } else return if_null;
    }

    private void setUpBackGround() {
        for (int i = 0; i < stars.length; i++) {stars[i] = new Star();}
    }
    private void showBackGround() {
        for (int i = 0; i < stars.length; i++) {stars[i].update(); stars[i].show(4, 0);}
    }

    public void mouseReleased(){
        try {player.cue(((int) map(mouseX, 0, width, 0, player.length())));
        } catch (Exception e){EException.append(e);}
    }

    private boolean toggle(boolean t) {return !t;}

    public void keyPressed(){
        float inc = 0.01f;
        keyDown = true;

        if (key == '2') {
            amplitude += 1;
        }

        if (key == '1') {
            amplitude -= 1;
            if (amplitude <= 0) amplitude = 1;
        }

        if (key == '6') {
            angOffset += .1;
        }

        if (key == '5') {
            angOffset -= .1;
            if (angOffset <= 1) angOffset = 1;
        }

        if (key == '8') {
            starKick += .5;
        }

        if (key == '7') {
            starKick -= .5;
            if (starKick <= 1) starKick = 1;
        }

        if(key == 'd'){
            player.skip(skip);
        }

        if(key == 'a'){
            player.skip(-skip);
        }

        if (key == 'w') {
            r += 1;
        }

        if (key == 's') {
            r -= 1;
            if (r <= 0) r = 1;
        }

        if (key == 'z'){
            showText = toggle(showText);
        }

        if (key == 'x'){
            visualMode++;
            if (visualMode > 3) visualMode = 0;
        }

        if (keyEvent.isControlDown() && keyEvent.isAltDown()) EException.getInstance(null);

        if (keyCode == UP) {
            strokeWeight += widthstepincrement;
        }

        if (keyCode == DOWN) {
            strokeWeight -= widthstepincrement;
            if (strokeWeight <= 1.0) strokeWeight = 1;
        }

        if (keyCode == LEFT) {
            barStep -= 1;
            if (barStep <= 0) barStep = 1;
        }

        if (keyCode == RIGHT) {
            barStep += 1;
        }

        if(keyCode == KeyEvent.VK_SPACE){
            if (player.isPlaying()) {player.pause(); isPaused = true;}
            else {player.play(); isPaused=false;}
        }

        if(key == '4' && smoothing < 1 - inc) smoothing += inc;
        if(key == '3' && smoothing > inc) smoothing -= inc;
    }



    public void keyReleased() {
        keyDown = false;

        if (key == 'v') {
            String filePath = fileChooser(music);
            if (!filePath.equals(music)) {
                player.pause();
                player = minim.loadFile(filePath, buffersize);
                fft = new FFT(player.bufferSize(), player.sampleRate());
                fft.logAverages(2000, 200);
                avgSize = fft.avgSize();
                fftSmooth = new float[avgSize];
                player.play();
            }
        }
    }

    private class Star {
        public float x;
        public float y;
        public float z;
        //public float pz;

        public Star() {
            x = random(-width / 2, width / 2);
            y = random(-height / 2, height / 2);
            z = random(width / 2);
            //pz = z;
        }

        public void update() {
            z -= speed;

            if (z <= 1) {
                z = random(width / 2);
                x = random(-width / 2, width / 2);
                y = random(-height / 2, height / 2);
                //pz = z;
            }
        }

        public void show(float max, float min) {
            float sx = map(x / z, 0, 1, 0, width / 2);
            float sy = map(y / z, 0, 1, 0, height / 2);
            float r = map(z, 0, width / 2, max, min);

            fill(map(r, max, min, 255, 0));
            noStroke();
            float added = map(ellipseR, 0, r * 2 - 50, 0, 2 * PI);
            //sx += starKick * cos(.015f );
            sy += starKick * sin(.06f * added);
            ellipse(sx + width / 2, sy + height / 2, r, r);
            //pz = z;
        }
    }
}