/*
* ============================================================================
*  Name        : CFriendlyNameApp from FriendlyNameApp.cpp
*  Part of     : FriendlyName
*  Created     : 29.04.2007 by Payu Sergey and Valerie Ivangorodsky
*  Description : Main application class.
*  Version     : 1.0
* ============================================================================
*/

// INCLUDE FILES
#include "FriendlyNameApp.h"          // CFriendlyNameApp
#include "FriendlyNameDocument.h"     // CFriendlyNameDocument

// ================= MEMBER FUNCTIONS ========================================
//
// ---------------------------------------------------------------------------
// CFriendlyNameApp::AppDllUid()
// Returns application UID.
// ---------------------------------------------------------------------------
//
TUid CFriendlyNameApp::AppDllUid() const
    {
    return KUidFriendlyName;
    }
  
// ---------------------------------------------------------------------------
// CFriendlyNameApp::CreateDocumentL()
// Creates CFriendlyNameDocument object.
// ---------------------------------------------------------------------------
//
CApaDocument* CFriendlyNameApp::CreateDocumentL()
    {
    return CFriendlyNameDocument::NewL(*this);
    }

// ================= OTHER EXPORTED FUNCTIONS ================================
//
// ---------------------------------------------------------------------------
// NewApplication() 
// Constructs CFriendlyNameApp.
// Returns: created application object.
// ---------------------------------------------------------------------------
//
EXPORT_C CApaApplication* NewApplication()
    {
    return new CFriendlyNameApp;
    }

// ---------------------------------------------------------------------------
// E32Dll(TDllReason) 
// Entry point function for EPOC Apps.
// Returns: KErrNone: No error
// ---------------------------------------------------------------------------
//
GLDEF_C TInt E32Dll(TDllReason)
    {
    return KErrNone;
    }

// End of File
