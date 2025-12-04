package it.unibo.oop.reactivegui02;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import it.unibo.oop.JFrameUtil;

import java.io.Serial;
import java.lang.reflect.InvocationTargetException;

/**
 * Second example of reactive GUI.
 */
public final class ConcurrentGUI extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentGUI.class);
    private final JLabel display = new JLabel();

    /**
     * Builds a ConcurrentGUI.
     */
    public ConcurrentGUI() {
        super();
        JFrameUtil.dimensionJFrame(this);
        final JPanel panel = new JPanel();
        panel.add(display);
        final JButton up = new JButton("up");
        final JButton down = new JButton("down");
        final JButton stop = new JButton("stop");

        panel.add(up);
        panel.add(down);
        panel.add(stop);

        this.getContentPane().add(panel);
        this.setVisible(true);

        final Agent agent = new Agent();
        new Thread(agent).start();

        up.addActionListener(e -> agent.setUpDirection());
        down.addActionListener(e -> agent.setDownDirection());
        stop.addActionListener(event -> {
            agent.stopCounting();
            up.setEnabled(false);
            down.setEnabled(false);
            stop.setEnabled(false);
        });
    }

    private final class Agent implements Runnable {

        private volatile boolean stop;
        private volatile boolean up = true;
        private int counter;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    final var nextInt = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(nextInt));
                    if (this.up) {
                        this.counter++;
                    } else {
                        this.counter--;
                    }
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }

        public void setUpDirection() {
            this.up = true;
        }

        public void setDownDirection() {
            this.up = false;
        }

        public void stopCounting() {
            this.stop = true;
        }
    }
}
