package matt.mvc;

import java.awt.event.*;

public class CalcController {
    //... The Controller needs to interact with both the Model and View.
    private CalcModel model;
    private CalcView  view;
    
    CalcController(CalcModel model, CalcView view) {
        this.model = model;
        this.view  = view;
        
        view.addMultiplyListener(new MultiplyListener());
        view.addClearListener(new ClearListener());
    }
    
    /** When a mulitplication is requested.
     *  1. Get the user input number from the View.
     *  2. Call the model to mulitply by this number.
     *  3. Get the result from the Model.
     *  4. Tell the View to display the result.
     * If there was an error, tell the View to display it.
     */
    class MultiplyListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String userInput = "";
            try {
                userInput = view.getUserInput();
                model.multiplyBy(userInput);
                view.setTotal(model.getValue());
            } catch (NumberFormatException nfex) {
                view.showError("Bad input: '" + userInput + "'");
            }
        }
    }
    
    class ClearListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	model.reset();
            view.reset();
        }
    }
}