/*
* ============================================================================
*  Name        : CFriendlyNameContainer from FriendlyNameContainer.h
*  Part of     : FriendlyName
*  Created     : 29.04.2007 by Payu Sergey and Valerie Ivangorodsky
*  Description : Declares container control for application.
*  Version     : 1.0
* ============================================================================
*/

#ifndef FRIENDLYNAMECONTAINER_H
#define FRIENDLYNAMECONTAINER_H

// INCLUDES
#include <coecntrl.h>

// FORWARD DECLARATIONS
class MAknsControlContext;

// CONSTANTS
const TInt KMaxNameLength = 0x40;

// CLASS DECLARATION
/**
* CFriendlyNameContainer container control class.
* Provides access to draw functions, displays data on screen.
*
*/
class CFriendlyNameContainer : public CCoeControl, MCoeControlObserver
    {
    public: // Constructor and destructor
        
        /**
        * ConstructL()
        * Default EPOC constructor.
        * @param aRect Frame rectangle for container.
        */
        void ConstructL(const TRect& aRect);

        /**
        * ~CFriendlyNameContainer()
        * Destructor.
        */
        ~CFriendlyNameContainer();

    private: // From CCoeControl

        /**
        * SizeChanged()
        * Responds to size changes.
        */
        void SizeChanged();

        /**
        * CountComponentControls()
        * Gets the number of controls contained in a compound control.
        * @return The number of component controls contained by this control.
        */
        TInt CountComponentControls() const;

        /**
        * ComponentControl()
        * Gets the specified component of a compound control.
        * @param aIndex The index of the control to get.
        * @return The component control with an index of aIndex.
        */
        CCoeControl* ComponentControl(TInt aIndex) const;

        /**
        * Draw()
        * Draws data to screen.
        * @param aRect The region of the control to be redrawn.
        */
        void Draw(const TRect& aRect) const;

        /**
        * MopSupplyObject()
        * Pass skin information if needed.
        * @param aId An encapsulated object type ID.
        * @return The pointer to the object provided.
        */
        TTypeUid::Ptr MopSupplyObject(TTypeUid aId);

    private: // From MCoeControlObserver

        /**
        * HandleControlEventL()
        * Acts upon changes in the hosted control's state. 
        * @param aControl The control changing its state.
        * @param aEventType The type of control event.
        */
        void HandleControlEventL(CCoeControl* aControl, TCoeEvent aEventType);
        
    public: // New functions

        /**
        * DisplayName()
        * Displays given name.
        * @param aDeviceName Device name.
        */
        void DisplayName(const TDesC& aDeviceName);

        /**
        * CurrentName()
        * Returns current name.
        * @return Current device name.
        */
        TPtrC CurrentName();
        
    private: // data
        
        MAknsControlContext*    iBackGround;
        TBuf<KMaxNameLength>    iDeviceName;
    };

#endif // FRIENDLYNAMECONTAINER_H

// End of File
