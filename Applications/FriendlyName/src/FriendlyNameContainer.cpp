/*
* ============================================================================
*  Name        : CFriendlyNameContainer from FriendlyNameContainer.cpp
*  Part of     : FriendlyName
*  Created     : 29.04.2007 by Payu Sergey and Valerie Ivangorodsky
*  Description : Container control implementation.
*  Version     : 1.0
* ============================================================================
*/

// INCLUDE FILES
#include "FriendlyNameContainer.h"      // CFriendlyNameContainer

#include <eikenv.h>                     // CEikonEnv
#include <aknsdrawutils.h> 
#include <aknsbasicbackgroundcontrolcontext.h>

// ================= MEMBER FUNCTIONS ========================================
//
// ---------------------------------------------------------------------------
// CFriendlyNameContainer::ConstructL(const TRect& aRect)
// Default EPOC constructor.
// ---------------------------------------------------------------------------
//
void CFriendlyNameContainer::ConstructL(const TRect& aRect)
    {
    CreateWindowL();

    iDeviceName.Zero();

    SetRect(aRect);
    iBackGround = CAknsBasicBackgroundControlContext::NewL( 
        KAknsIIDQsnBgAreaMain, Rect(), EFalse);
    ActivateL();
    }

// ---------------------------------------------------------------------------
// CFriendlyNameContainer::~CFriendlyNameContainer()
// Destructor.
// ---------------------------------------------------------------------------
//
CFriendlyNameContainer::~CFriendlyNameContainer()
    {
    delete iBackGround;
    }

// ---------------------------------------------------------------------------
// CFriendlyNameContainer::SizeChanged()
// Called by framework when the view size is changed.
// ---------------------------------------------------------------------------
//
void CFriendlyNameContainer::SizeChanged()
    {
    }

// ---------------------------------------------------------------------------
// CFriendlyNameContainer::CountComponentControls() const
// Gets the number of controls contained in a compound control.
// ---------------------------------------------------------------------------
//
TInt CFriendlyNameContainer::CountComponentControls() const
    {
    // return nbr of controls inside this container
    return 0; 
    }

// ---------------------------------------------------------------------------
// CFriendlyNameContainer::ComponentControl(TInt aIndex) const
// Gets the specified component of a compound control.
// ---------------------------------------------------------------------------
//
CCoeControl* CFriendlyNameContainer::ComponentControl(TInt /*aIndex*/) const
    {
    return NULL;
    }

// ---------------------------------------------------------------------------
// CFriendlyNameContainer::Draw(const TRect& aRect) const
// Draws data to screen.
// ---------------------------------------------------------------------------
//
void CFriendlyNameContainer::Draw(const TRect& aRect) const
    {
    CWindowGc& gc = SystemGc();
    
    MAknsSkinInstance* skin = AknsUtils::SkinInstance();
	MAknsControlContext* cc = AknsDrawUtils::ControlContext(this);
    AknsDrawUtils::Background(skin, cc, this, gc, aRect);

    const CFont* font = CEikonEnv::Static()->NormalFont();
    gc.UseFont(font);

    gc.SetPenColor(KRgbBlack);
    TInt len = font->TextWidthInPixels(iDeviceName);
    TInt hgt = font->HeightInPixels();
    gc.DrawText(iDeviceName, TPoint((aRect.Width() - len)/2, (aRect.Height() - hgt)/2));
    }

// ---------------------------------------------------------------------------
// CFriendlyNameContainer::HandleControlEventL(
//     CCoeControl* aControl, TCoeEvent aEventType)
// ---------------------------------------------------------------------------
//
void CFriendlyNameContainer::HandleControlEventL(
    CCoeControl* /*aControl*/, TCoeEvent /*aEventType*/)
    {
    }

// ---------------------------------------------------------------------------
// CFriendlyNameContainer::MopSupplyObject()
// Pass skin information if needed.
// ---------------------------------------------------------------------------
//
TTypeUid::Ptr CFriendlyNameContainer::MopSupplyObject(TTypeUid aId)
    {
    if (aId.iUid == MAknsControlContext::ETypeId && iBackGround)
        {
        return MAknsControlContext::SupplyMopObject(aId, iBackGround);
        }

    return CCoeControl::MopSupplyObject(aId);
    }

// ---------------------------------------------------------------------------
// CFriendlyNameContainer::DisplayName()
// Displays given name.
// ---------------------------------------------------------------------------
//
void CFriendlyNameContainer::DisplayName(const TDesC& aDeviceName)
    {
    iDeviceName.Copy(aDeviceName);
    DrawNow();
    }

// ---------------------------------------------------------------------------
// CFriendlyNameContainer::CurrentName()
//  Returns current name.
// ---------------------------------------------------------------------------
//
TPtrC CFriendlyNameContainer::CurrentName()
    {
    return iDeviceName;
    }

// End of File
