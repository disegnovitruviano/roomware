/*
* ============================================================================
*  Name        : CFriendlyNameAppUi from FriendlyNameAppui.h
*  Part of     : FriendlyName
*  Created     : 29.04.2007 by Payu Sergey and Valerie Ivangorodsky
*  Description : Declares UI class for application.
*  Version     : 1.0
* ============================================================================
*/

#ifndef FRIENDLYNAMEAPPUI_H
#define FRIENDLYNAMEAPPUI_H

// INCLUDES
#include <aknappui.h>

// FORWARD DECLARATIONS
class CFriendlyNameContainer;

// LIBS
// bteng.lib

// CLASS DECLARATION
/**
* CFriendlyNameAppUi application UI class.
* Provides support for the following features:
* - EIKON control architecture.
* 
*/
class CFriendlyNameAppUi : public CAknAppUi
    {
    public: // Constructor and destructor

        /**
        * ConstructL()
        * Default EPOC constructor.
        */      
        void ConstructL();

        /**
        * ~CFriendlyNameAppUi()
        * Destructor.
        */      
        ~CFriendlyNameAppUi();

    private: // From MEikMenuObserver
        
        /**
        * DynInitMenuPaneL()
        * Takes care of dynamic menu handling.
        * @param aResourceId Menu's resource id.
        * @param aMenuPane Reference to dynamic menu.
        */
        void DynInitMenuPaneL(TInt aResourceId, CEikMenuPane* aMenuPane);

    private: // From CEikAppUi
        
        /**
        * HandleCommandL()
        * Takes care of command handling.
        * @param aCommand command to be handled.
        */
        void HandleCommandL(TInt aCommand);

        /**
        * HandleKeyEventL()
        * Handles key events.
        * @param aKeyEvent Event to handled.
        * @param aType Type of the key event. 
        * @return Response code (EKeyWasConsumed, EKeyWasNotConsumed). 
        */
        virtual TKeyResponse HandleKeyEventL(const TKeyEvent& aKeyEvent,
            TEventCode aType);
           
    public: // New functions

        /**
        * GetNameL()
        * Retrieves device name and displays it on the screen.
        */
        void GetNameL();

        /**
        * SetNameL()
        * Sets given name as device name.
        * @param aDeviceName Device name.
        */
        void SetNameL(const TDesC& aDeviceName);

    private: // data
        
        CFriendlyNameContainer*     iAppContainer; 
    };

#endif // FRIENDLYNAMEAPPUI_H

// End of File
