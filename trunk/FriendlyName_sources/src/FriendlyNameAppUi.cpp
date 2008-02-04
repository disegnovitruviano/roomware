/*
* ============================================================================
*  Name        : CFriendlyNameAppUi from FriendlyNameAppui.cpp
*  Part of     : FriendlyName
*  Created     : 29.04.2007 by Payu Sergey and Valerie Ivangorodsky
*  Description : CFriendlyNameAppUi implementation.
*  Version     : 1.0
* ============================================================================
*/

// INCLUDE FILES
#include "FriendlyNameAppUi.h"              // CFriendlyNameAppUi
#include "FriendlyNameContainer.h"          // CFriendlyNameContainer
#include "FriendlyName.hrh"

#include "BTMCMSettings.h"                  // CBTMCMSettings
#include <aknquerydialog.h>                 // CAknTextQueryDialog

#include <FriendlyName.rsg>
#include <avkon.hrh>
#include <eikmenup.h>                       // CEikMenuPane

// ================= MEMBER FUNCTIONS ========================================
//
// ---------------------------------------------------------------------------
// CFriendlyNameAppUi::ConstructL()
// Default EPOC constructor.
// ---------------------------------------------------------------------------
//
void CFriendlyNameAppUi::ConstructL()
    {
    BaseConstructL(EAknEnableSkin);

    iAppContainer = new (ELeave) CFriendlyNameContainer;
    iAppContainer->SetMopParent(this);
    iAppContainer->ConstructL(ClientRect());
    AddToStackL(iAppContainer);

    TRAPD(err, GetNameL());
    }

// ---------------------------------------------------------------------------
// CFriendlyNameAppUi::~CFriendlyNameAppUi()
// Destructor.
// ---------------------------------------------------------------------------
//
CFriendlyNameAppUi::~CFriendlyNameAppUi()
    {
    if (iAppContainer)
        {
        RemoveFromStack(iAppContainer);
        delete iAppContainer;
        }
    }

// ---------------------------------------------------------------------------
// CFriendlyNameAppUi::DynInitMenuPaneL(TInt aResourceId, 
//                                     CEikMenuPane* aMenuPane)
// Dynamically initialises a menu pane.
// ---------------------------------------------------------------------------
//
void CFriendlyNameAppUi::DynInitMenuPaneL(TInt /*aResourceId*/, 
                                         CEikMenuPane* /*aMenuPane*/)
    {
    }

// ---------------------------------------------------------------------------
// CFriendlyNameAppUi::HandleKeyEventL(const TKeyEvent& aKeyEvent,
//                                       TEventCode /*aType*/)
// Takes care of key event handling.
// ---------------------------------------------------------------------------
//
TKeyResponse CFriendlyNameAppUi::HandleKeyEventL(
    const TKeyEvent& /*aKeyEvent*/, TEventCode /*aType*/)
    {
    return EKeyWasNotConsumed;
    }

// ---------------------------------------------------------------------------
// CFriendlyNameAppUi::HandleCommandL(TInt aCommand)
// Takes care of command handling.
// ---------------------------------------------------------------------------
//
void CFriendlyNameAppUi::HandleCommandL(TInt aCommand)
    {
    switch (aCommand)
        {
        case EAknSoftkeyExit:
        case EEikCmdExit:
            {
            Exit();
            break;
            }
            
        case EFriendlyNameCmdChangeName:
            {
            THostName name(iAppContainer->CurrentName());
            CAknTextQueryDialog* dialog = 
                CAknTextQueryDialog::NewL(name);
            if (dialog->ExecuteLD(R_FRIENDLYNAME_DEVICE_NAME_QUERY)) 
                {
                SetNameL(name);
                }
            break;
            }
            
        default:
            break;      
        }
    }

// ---------------------------------------------------------------------------
// CFriendlyNameAppUi::GetNameL()
// Retrieves device name and displays it on the screen.
// ---------------------------------------------------------------------------
//
void CFriendlyNameAppUi::GetNameL()
    {
    TBuf<KMaxNameLength> name;
    User::LeaveIfError(CBTMCMSettings::GetLocalBTName(name));

    iAppContainer->DisplayName(name);
    }

// ---------------------------------------------------------------------------
// CFriendlyNameAppUi::SetNameL(const TDesC& aDeviceName)
// Sets given name as device name.
// ---------------------------------------------------------------------------
//
void CFriendlyNameAppUi::SetNameL(const TDesC& aDeviceName)
    {
    CBTMCMSettings* btSettings = CBTMCMSettings::NewLC(NULL);
    User::LeaveIfError(btSettings->SetLocalBTName(aDeviceName));
    CleanupStack::PopAndDestroy(btSettings);

    GetNameL();
    }

// End of File
