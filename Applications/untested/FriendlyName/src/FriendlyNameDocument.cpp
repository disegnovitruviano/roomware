/*
* ============================================================================
*  Name        : CFriendlyNameDocument from FriendlyNameDocument.cpp
*  Part of     : FriendlyName
*  Created     : 29.04.2007 by Payu Sergey and Valerie Ivangorodsky
*  Description : CFriendlyNameDocument implementation.
*  Version     : 1.0
* ============================================================================
*/

// INCLUDE FILES
#include "FriendlyNameDocument.h"     // CFriendlyNameDocument
#include "FriendlyNameAppui.h"        // CFriendlyNameAppui

// ================= MEMBER FUNCTIONS ========================================
//
// ---------------------------------------------------------------------------
// CFriendlyNameDocument::CFriendlyNameDocument(CEikApplication& aApp)
// Default C++ constructor.
// ---------------------------------------------------------------------------
//
CFriendlyNameDocument::CFriendlyNameDocument(CEikApplication& aApp) : 
    CAknDocument(aApp)    
    {
    }

// ---------------------------------------------------------------------------
// CFriendlyNameDocument::~CFriendlyNameDocument()
// Destructor.
// ---------------------------------------------------------------------------
//
CFriendlyNameDocument::~CFriendlyNameDocument()
    {
    }

// ---------------------------------------------------------------------------
// CFriendlyNameDocument::ConstructL()
// Default EPOC constructor.
// ---------------------------------------------------------------------------
//
void CFriendlyNameDocument::ConstructL()
    {
    }

// ---------------------------------------------------------------------------
// CFriendlyNameDocument::NewL(CEikApplication& aApp)
// Two-phased constructor.
// ---------------------------------------------------------------------------
//
CFriendlyNameDocument* CFriendlyNameDocument::NewL(
    CEikApplication& aApp) // CFriendlyNameApp reference
    {
    CFriendlyNameDocument* self = 
        new (ELeave) CFriendlyNameDocument(aApp);
    CleanupStack::PushL(self);
    self->ConstructL();
    CleanupStack::Pop();

    return self;
    }
    
// ---------------------------------------------------------------------------
// CFriendlyNameDocument::CreateAppUiL()
// Constructs CFriendlyNameAppUi.
// ---------------------------------------------------------------------------
//
CEikAppUi* CFriendlyNameDocument::CreateAppUiL()
    {
    return new (ELeave) CFriendlyNameAppUi;
    }
    
// End of File
