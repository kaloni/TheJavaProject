import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;


public class SidePanel extends JPanel implements ActionListener {

	private GUI parent;
	private JToolBar toolBar;
	
	public SidePanel(GUI parent) {
		
		super(new BorderLayout());
		this.parent = parent;
		//setPreferredSize(new Dimension(300, 600));
		toolBar = new JToolBar();
		initButtons(5);
		add(toolBar);
		
	}

	public void actionPerformed(ActionEvent e) {
		
	}
	
	private void initButtons(int buttons) {
		
		for(int i = 0; i < buttons; i++) {
			toolBar.add(new JButton(Integer.toString(i)));
		}
		
	}
	
	
}
