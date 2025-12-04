package it.unibo.oop.reactivegui03;

import java.io.Serial;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unibo.oop.JFrameUtil;

/**
 * Third experiment with reactive gui.
 */
public final class AnotherConcurrentGUI extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(AnotherConcurrentGUI.class);
    private final JLabel display = new JLabel();
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    private final JButton stop = new JButton("stop");

    public AnotherConcurrentGUI() {
        super();
        JFrameUtil.dimensionJFrame(this);
        final JPanel panel = new JPanel();
        panel.add(display);

        panel.add(up);
        panel.add(down);
        panel.add(stop);

        this.getContentPane().add(panel);
        this.setVisible(true);

        final Agent agent1 = new Agent();
        final StopAgent agent2 = new StopAgent(agent1);

        new Thread(agent1).start();
        new Thread(agent2).start();

        up.addActionListener(e -> agent1.setUpDirection());
        down.addActionListener(e -> agent1.setDownDirection());
        stop.addActionListener(e -> {
            agent1.stopCounting();
            up.setEnabled(false);
            down.setEnabled(false);
            stop.setEnabled(false);
        });

    }

    private final class Agent implements Runnable {

        private static final int STOP_PERIOD = 100;
        private int counter = 0;
        private volatile boolean up = true;
        private volatile boolean stop;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    final var nextInt = Integer.toString(counter);
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextInt));
                    if (this.up) {
                        this.counter++;
                    } else {
                        this.counter--;
                    }
                    Thread.sleep(Agent.STOP_PERIOD);
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

        public boolean isStopped() {
            return this.stop;
        }
    }

    private final class StopAgent implements Runnable {

        private static final int DEAD_TIME = 10000;
        private final Agent target;

        StopAgent(final Agent target) {
            this.target = target;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(StopAgent.DEAD_TIME);
                if (!target.isStopped()) {
                    this.target.stopCounting();
                    AnotherConcurrentGUI.this.up.setEnabled(false);
                    AnotherConcurrentGUI.this.down.setEnabled(false);
                    AnotherConcurrentGUI.this.stop.setEnabled(false);
                }
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

    }
}
