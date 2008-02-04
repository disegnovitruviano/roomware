/*
* ============================================================================
*  Name        : CFriendlyNameApp from FriendlyNameApp.h
*  Part of     : FriendlyName
*  Created     : 29.04.2007 by Payu Sergey and Valerie Ivangorodsky
*  Description : Declares main application class.
*  Version     : 1.0
* ============================================================================
*/

#ifndef FRIENDLYNAMEAPP_H
#define FRIENDLYNAMEAPP_H

// INCLUDES
#include <aknapp.h>

// CONSTANTS
// UID of the application
const TUid KUidFriendlyName = { 0x041c1f1b };

// CLASS DECLARATION
/**
* CFriendlyNameApp application class.
* Provides factory to create concrete document object.
* 
*/
class CFriendlyNameApp : public CAknApplication
    {
    private: // From CApaApplication

        /**
        * CreateDocumentL()
        * Creates CFriendlyNameDocument document object.
        * @return A pointer to the created document object.
        */
        CApaDocument* CreateDocumentL();
        
        /**
        * CApaApplication()
        * Returns application's UID (KUidFriendlyName).
        * @return The value of KUidFriendlyName.
        */
        TUid AppDllUid() const;
    };

#endif // FRIENDLYNAMEAPP_H

// End of File
