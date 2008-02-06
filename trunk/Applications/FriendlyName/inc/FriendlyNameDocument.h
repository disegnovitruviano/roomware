/*
* ============================================================================
*  Name        : CFriendlyNameDocument from FriendlyNameDocument.h
*  Part of     : FriendlyName
*  Created     : 29.04.2007 by Payu Sergey and Valerie Ivangorodsky
*  Description : Declares document for application.
*  Version     : 1.0
* ============================================================================
*/

#ifndef FRIENDLYNAMEDOCUMENT_H
#define FRIENDLYNAMEDOCUMENT_H

// INCLUDES
#include <akndoc.h>

// FORWARD DECLARATIONS
class  CEikAppUi;

// CLASS DECLARATION
/**
* CFriendlyNameDocument application class.
* Represents the data model.
*
*/
class CFriendlyNameDocument : public CAknDocument
    {
    public: // Constructor and destructor
        
        /**
        * NewL()
        * Two-phased constructor.
        */
        static CFriendlyNameDocument* NewL(CEikApplication& aApp);

        /**
        * ~CFriendlyNameDocument()
        * Destructor.
        */
        virtual ~CFriendlyNameDocument();

    private: // Constructors

        /**
        * CFriendlyNameDocument()
        * Default C++ constructor.
        */
        CFriendlyNameDocument(CEikApplication& aApp);
        
        /**
        * ConstructL()
        * Default EPOC constructor.
        */
        void ConstructL();

    private: // From CEikDocument

        /**
        * CreateAppUiL()
        * Creates CFriendlyNameAppUi "App UI" object.
        * @return A pointer to the created "App UI" object.
        */
        CEikAppUi* CreateAppUiL();
    };

#endif // FRIENDLYNAMEDOCUMENT_H

// End of File
