public with sharing class VolunteerJoinMembershipInfoController extends SobjectExtension {
    public String membershipProduct {get; set;}
    public String firstName {get; set;}
    public String lastName {get; set;}

    public String streetLine1 {get; set;}
    public String streetLine2 {get; set;}

    public String dateOfBirth {get; set;}
    public String gender {get; set;}
    public String email {get; set;}
    public String email2 {get; set;}
    public String preferredEmail {get; set;}

    public String city {get; set;}
    public String state {get; set;}
    public String homePhone {get; set;}
    public String zipCode {get; set;}
    public String county {get; set;}
    public String country {get; set;}
    public String preferredPhone {get; set;}
    public String businessPhone {get; set;}
    public String mobilePhone {get; set;}
    public Account councilAccount {get; set;}

    public Boolean booleanTermsAndConditions {get; set;}
    public Boolean booleanOppMembershipOnPaper {get; set;}
    public Boolean booleanGrantRequested {get; set;}

    public Boolean booleanContactEmailOptIn {get; set;}
    public Boolean booleanContactTextPhoneOptIn {get; set;}
    public Boolean booleanContactPhotoOptIn {get; set;}
    public Boolean financialCheckBox {get;set;}
    public Boolean isCouncilTermsConditionsAvailable {get;set;}
    public Boolean isLifetime { get; set; }
    public List<Opportunity> memberOpportunityList;
    public Opportunity lifetimeOpportunity;
    
    public PricebookEntry[] PricebookEntryList;
    public map<Id, PricebookEntry> priceBookEntryMap;

    public List<SelectOption> getmembershipProductList() {
        List<SelectOption> membershipProducts = new List<SelectOption>();
        

        if(isLifetime) {
            List<OpportunityLineItem> lifetimeOLIList = [
                Select Id
                     , OpportunityId
                     , Item_Type__c
                     , PricebookEntryId
                     , PricebookEntry.Id
                     , PricebookEntry.Name
                  From OpportunityLineItem
                 where OpportunityId = :lifetimeOpportunity.Id
                 limit 1
            ];

            OpportunityLineItem OppLI;
            if(lifetimeOLIList.size() > 0) {
                OppLI = lifetimeOLIList[0];
            }
            for(PricebookEntry pricebookEntry : PricebookEntryList) {
                if (OppLI != null && OppLI.PricebookEntryId == pricebookEntry.Id)
                    membershipProducts.add(new SelectOption(pricebookEntry.Id, '$'+pricebookEntry.UnitPrice +' ' +pricebookEntry.Name));
            }
            if(lifetimeOLIList != null && lifetimeOLIList.size() > 0)
                membershipProduct = lifetimeOLIList[0].Id;
        }else{
            membershipProducts.add(new SelectOption('--None--', '--None--'));
            for(PricebookEntry pricebookEntry : PricebookEntryList) {
                if (pricebookEntry.Name.toUpperCase().contains('ADULT') || pricebookEntry.Name.toUpperCase().contains('LIFETIME'))
                    membershipProducts.add(new SelectOption(pricebookEntry.Id, '$'+pricebookEntry.UnitPrice +' ' +pricebookEntry.Name));
            }
        }
        
        return membershipProducts;
    }

    public String readAndAgree {get; set;}
    public String termsAndCondition {get; set;}
    public String memberShipOnPaper {get; set;}
    public String grantRequested {get; set;}
    public String thankYou {get; set;}

    public String term1 {get; set;}
    public String term2 {get; set;}
    public String term3 {get; set;}
    public String term4 {get; set;}
    public Boolean tremsflag {get; set;}
    public Contact contact;

    private Zip_Code__c matchingZipCode = new Zip_Code__c();
    private User defaultSystemAdminUser = new User();

    public static final string MEMBERSHIP_RECORDTYPE = 'Membership';
    public static final string GIRLSCOUTSUSA = 'Girl Scouts USA';
    public static final string LIFETIME_MEMBERSHIP = 'Lifetime Membership';

    private static Integer counterUnableToLockRow = 0;
    private static final map<String, Schema.RecordTypeInfo> OPPORTUNITY_RECORDTYPE_INFO_MAP =  Opportunity.SObjectType.getDescribe().getRecordTypeInfosByName();

    private static String getOppRecordTypeId(String name) {
       return (OPPORTUNITY_RECORDTYPE_INFO_MAP.get(name) != null) ? OPPORTUNITY_RECORDTYPE_INFO_MAP.get(name).getRecordTypeId() : null;
    }

    public VolunteerJoinMembershipInfoController() {

        String contactId;

        counterUnableToLockRow = 0;
        tremsflag = false;
        isLifetime = false;

        booleanContactEmailOptIn = true;
        booleanGrantRequested = false;
        booleanContactPhotoOptIn = true;
        booleanOppMembershipOnPaper = false;
        readAndAgree = 'value that specifies whether this checkbox 10.0 global should ';

        term1 = 'I wish to opt out at this time. I will abide by the Girl Scout Promise and Law.';
        term2 = 'I will follow all Girl Scouts (Council Name Here) policies in my role as a volunteer.';
        term3 = 'I agree to complete a background check in order to help ensure the safety of the youth served through Girl Scouts.';
        term4 = 'I will respect and maintain the confindentiality of all previleged information related to Girls Scouts, its girls, its Volunteers or staff to which I am exposed while serving as a volunteer.';

        termsAndCondition = 'I accept and abide by the Girl Scouts Promise and Law.';
        memberShipOnPaper = 'If you prefer not to pay by credit card, click here to pay by cash/check.';
        grantRequested = 'I request financial assistance for membership registration';
        thankYou = 'Value that specifies whether this checkbox 10.0 global should /n 2.value that specifies whether this checkbox 10.0 global should';

        defaultSystemAdminUser = VolunteerRegistrationUtilty.getSystemAdminUser();

        priceBookEntryMap = new map<Id, PricebookEntry>();
        PricebookEntryList = new PricebookEntry[]{};
        fillPricebookEntryList();

        if (Apexpages.currentPage().getParameters().containsKey('CouncilId')) {
            councilAccount = VolunteerRegistrationUtilty.getCouncilAccount(Apexpages.currentPage().getParameters().get('CouncilId'));
            VolunteerController.councilAccount = councilAccount;
        }

        if (councilAccount != null) {
            if (councilAccount.Terms_Conditions__c != null)
                tremsflag = true;
        }

        isCouncilTermsConditionsAvailable = (councilAccount != null && councilAccount.Terms_Conditions__c != null && councilAccount.Terms_Conditions__c != '') ? true  : false;

        financialCheckBox  = (councilAccount != null && councilAccount.Volunteer_Financial_Aid_Available__c != null && councilAccount.Volunteer_Financial_Aid_Available__c == true)
                             ? true
                             : false;

        if (Apexpages.currentPage().getParameters().containsKey('ContactId')) {
            contactId = Apexpages.currentPage().getParameters().get('ContactId');
        }
        system.debug('==contactId===:  ' + contactId);

        if (contactId != null && contactId != '')
            contact = getContact(contactId);

        system.debug('==contact===:  ' + contact);

        if (contact != null) {
            firstName = (contact.FirstName != null) ? contact.FirstName : '';
            lastName = (contact.LastName != null) ? contact.LastName : '';
            email = (contact.rC_Bios__Home_Email__c != null) ? contact.rC_Bios__Home_Email__c : '';
            preferredEmail = (contact.rC_Bios__Preferred_Email__c != null) ? contact.rC_Bios__Preferred_Email__c : '';
            zipCode = (contact.MailingPostalCode != null) ? contact.MailingPostalCode : '';
            homePhone = (contact.HomePhone != null) ? contact.HomePhone : '';
            preferredEmail =  'Email';
            preferredPhone =  'Home Phone';
        }

        if(contact != null){
             memberOpportunityList= getLifetimeMember(contact);
             if(memberOpportunityList != null && memberOpportunityList.size() > 0){
                 isLifetime = true;
                 lifetimeOpportunity = memberOpportunityList[0];
             }
             else{
                 isLifetime = false;
             }
        }
        
        Pricebook2[] pricebookList = [
            Select Name
                 , IsActive
                 , Id
              From Pricebook2
             where IsActive = true
               and Name = :GIRLSCOUTSUSA
        ];

        system.debug('==contactId :  ' + contactId);
    }

    public List<Opportunity> getLifetimeMember(Contact contact){
        String membershipRecordTypeId =  getOppRecordTypeId(MEMBERSHIP_RECORDTYPE);

        memberOpportunityList = [
            Select Id
                 , Contact__c
                 , Background_Check__c
                 , Background_Check_Completion_Date__c
                 , Background_Check_Status__c
              From Opportunity
             Where Contact__c =: contact.Id
               And Type = 'Lifetime Membership'
               And RecordTypeId =: membershipRecordTypeId
          order by CreatedDate desc
        ];
        system.debug('memberOpportunityList===>'+memberOpportunityList);
        return (memberOpportunityList != null && memberOpportunityList.size() > 0) ? memberOpportunityList : new List<Opportunity>();
    }
    
    public void fillPricebookEntryList() {
        PricebookEntryList = [
            Select Id
                 , Name
                 , UnitPrice
                 , Product2Id
                 , Pricebook2Id
                 , Pricebook2.Id
                 , Pricebook2.Name
                 , Pricebook2.IsActive
                 , Pricebook2.Description
                 , Product2.rC_Giving__Start_Date__c
                 , Product2.rC_Giving__End_Date__c
                 , Product2.Name
              From PricebookEntry
             where Pricebook2.Name = :GIRLSCOUTSUSA
               and Pricebook2.IsActive = true
               and IsActive = true
        ];

        system.debug('PricebookEntryList ========>'+PricebookEntryList );
        if (PricebookEntryList != null && PricebookEntryList.size() >0) {
            for(PricebookEntry pricebookEntry : PricebookEntryList) {
                system.debug('Date========> ' + pricebookEntry.Product2.rC_Giving__End_Date__c);
                priceBookEntryMap.put(pricebookEntry.Id, pricebookEntry);
            }
        }
        system.debug('priceBookEntryMap========>'+priceBookEntryMap);
    }

    public List<SelectOption> getGenders() {
        List<SelectOption> genderOptions = new List<SelectOption>();
        genderOptions.add(new SelectOption('Female', 'Female'));
        genderOptions.add(new SelectOption('Male', 'Male'));
        return genderOptions;
    }

    public List<SelectOption> getPreferredEmails() {
        List<SelectOption> emailOptions = new List<SelectOption>();
        emailOptions.add(new SelectOption('--None--', '--None--'));
        emailOptions.add(new SelectOption('Email', 'Email'));
        emailOptions.add(new SelectOption('Email2', 'Email2'));
        return emailOptions;
    }

    public List<SelectOption> getPreferredPhones() {
        List<SelectOption> phoneOptions = new List<SelectOption>();

        phoneOptions.add(new SelectOption('--None--', '--None--'));
        phoneOptions.add(new SelectOption('Home Phone', 'Home Phone'));
        phoneOptions.add(new SelectOption('Business Phone', 'Business Phone'));
        phoneOptions.add(new SelectOption('Mobile Phone', 'Mobile Phone'));
        return phoneOptions;
    }

    public Pagereference submit() {
        counterUnableToLockRow++;
        Savepoint savepoint = Database.setSavepoint();

        try {
            if(preferredEmail.equalsIgnoreCase('Email2') && email2 == '') {
                return addErrorMessage('Email 2 cannot be left blank when selecting Email 2 as Preferred.');
            }
            if(preferredPhone != null && preferredPhone.equalsIgnoreCase('Business Phone')) {
                if(businessPhone == null || businessPhone == '')
                    return addErrorMessage('Business phone cannot be left blank when selecting Business Phone as Preferred.');
            }
            if(preferredPhone != null && preferredPhone.equalsIgnoreCase('Mobile Phone')) {
                if(mobilePhone == null || mobilePhone == '')
                    return addErrorMessage('Mobile phone cannot be left blank when selecting Mobile Phone as Preferred.');
            }
            if(contact != null && contact.MailingPostalCode != null)
                zipCode = contact.MailingPostalCode;

            Opportunity newOpportunity = new Opportunity();
            OpportunityLineItem opportunityLineItem = new OpportunityLineItem();
            PricebookEntry priceBookEntry = new PricebookEntry();
            Account account = new Account();
            CampaignMember[] campaignMemberList = new CampaignMember[]{};
            CampaignMember[] renewCampaignMembersList;
            set<Id> ContactAddressIdSet = new set<Id>();
            Set<String> campaignMemberIdSet = new Set<String>();
            String[] campaignMemberIdList;
            String campaignMembers;

            if (Apexpages.currentPage().getParameters().containsKey('CampaignMemberIds')) {
                campaignMembers = Apexpages.currentPage().getParameters().get('CampaignMemberIds');
            }

            if (campaignMembers != null && campaignMembers != '') {
                campaignMemberIdList = campaignMembers.trim().split(',');
                for(String campaignMember : campaignMemberIdList) {
                    if(campaignMember != null)
                        campaignMemberIdSet.add(campaignMember.trim());
                }
            }
            system.debug('%%%% membershipProduct'+membershipProduct);
            if (priceBookEntryMap != null && priceBookEntryMap.size() > 0) {
               if ( !membershipProduct.toUpperCase().contains('NONE') && priceBookEntryMap.containsKey(membershipProduct.trim()))
                   priceBookEntry = priceBookEntryMap.get(membershipProduct.trim());
            }

            if(booleanTermsAndConditions == false) {
                 return addErrorMessageAndRollback(savepoint,'To Proceed, You must agree to accept and abide by the Girl Scout Promise and Law.');
            }

            system.debug('********************contact : ' + contact + '******priceBookEntry : ' + priceBookEntry);

            if (contact == null)
                 return addErrorMessageAndRollback(savepoint,'Contact does not exist.');
            if (priceBookEntry == null)
                 return addErrorMessageAndRollback(savepoint,'Product is not selected.');

            // Added to avoid duplicate membership
            string membershipYear = '';
            if(priceBookEntry != null && priceBookEntry.Product2.rC_Giving__End_Date__c != null)
                membershipYear = string.valueOf(priceBookEntry.Product2.rC_Giving__End_Date__c.year());
                system.debug('***membershipYear***'+membershipYear);
            if(membershipYear !=null && membershipYear != '') {
                List<campaignmember> lstCM = [Select Membership__r.Membership_year__c from campaignmember where ContactId =:contact.ID];
                    system.debug('***lstCM***'+lstCM);
                if(lstCM.size()>0) {
                    for(campaignmember cm : lstCM) {
                         system.debug('***cm.Membership__r.Membership_year__c***'+cm.Membership__r.Membership_year__c); 
                        if(cm.Membership__r.Membership_year__c == membershipYear)
                            return addErrorMessageAndRollback(savepoint,'This membership is already active, Please select another membership.');
                            system.debug('***after error***');
                    }
                }
            }

            if(contact != null && contact.AccountId != null) {
                Account[] accountList = [
                    Select Id
                         , Name
                         , Phone
                         , ParentId
                         , Instructions__c
                      From Account
                     where Id = :contact.AccountId
                     limit 1
                ];

                Zip_Code__c[] zipCodeList = new Zip_Code__c[] {};
                if(zipCode != null && zipCode != '')
                    zipCode = (zipCode.length() > 5) ? zipCode.substring(0, 5) : zipCode;
                    zipCodeList = [
                        Select Id
                             , Name
                             , Council__c
                             , Zip_Code_Unique__c
                             , City__c
                             , Recruiter__c
                             , Recruiter__r.IsActive
                             , Recruiter__r.UserRoleId
                          From Zip_Code__c
                         where Zip_Code_Unique__c like :zipCode limit 1
                    ];

                matchingZipCode = (zipCodeList.size() > 0) ? zipCodeList[0] : new Zip_Code__c();
                account = (accountList != null) ? accountList[0] : null;
                contact = updateContact(contact);
                if(isLifetime == false) {
                    newOpportunity = createMembershipOpportunity(getOppRecordTypeId(MEMBERSHIP_RECORDTYPE), contact, priceBookEntry);
                    system.debug('newOpportunity ===: ' + newOpportunity);
                    if(newOpportunity == null) {
                        return addErrorMessageAndRollback(savepoint,'Membership Opportunity not created.');
                    }

                    campaignMemberList =  getCampaignMember(contact.Id, campaignMemberIdSet);

                    rC_Bios__Contact_Address__c contactAddress = createContactAddress(contact, 'Home', streetLine1, streetLine2, city, state, zipCode, country, null);

                    if(contactAddress != null && contactAddress.Id != null)
                        ContactAddressIdSet.add(contactAddress.Id);

                    List<rC_Bios__Contact_Address__c> contactAddressList = [
                        Select Id
                             , rC_Bios__Contact__r.MailingPostalCode
                             , rC_Bios__Contact__c
                             , rC_Bios__Address__c
                          From rC_Bios__Contact_Address__c
                        where Id IN :ContactAddressIdSet
                    ];
                    //Update address owner
                    if(contactAddressList != null && contactAddressList.size() > 0)
                        updateAddressOwner(contactAddressList);

                    if (membershipProduct != null && newOpportunity != null)
                        opportunityLineItem = createOpportunityLineItem(priceBookEntry, newOpportunity);

                    updateOpportunityType(newOpportunity, priceBookEntry, contact);

                    if(campaignMemberList != null && campaignMemberList.size() > 0)
                        updateCampaignMembers(campaignMemberList, newOpportunity);
                }else{
                    if(contact != null){
                        renewCampaignMembersList = getCampaignMembers(contact.Id, campaignMemberIdSet);
                        system.debug('***renewCampaignMembersList***'+renewCampaignMembersList);
                        if(renewCampaignMembersList != null && renewCampaignMembersList.size() > 0){
                            system.debug('***12345***');    
                            updateCampaignMembers(renewCampaignMembersList, lifetimeOpportunity);
                        }
                    }
                }
                

                //Update council account
                if (booleanGrantRequested == true && councilAccount != null)
                    updateCouncilAccountsFinancialAid(councilAccount.Id, booleanGrantRequested);

                    /* - Commneted By Siddhant -- Added update of councilAccount to future method
                    updateCouncilAccount(councilAccount, booleanGrantRequested); */

                //GSA-390 : Create a OpportunityContactRole
                if(newOpportunity != null && contact != null && newOpportunity.Id != null && contact.Id != null){
                    OpportunityContactRole opportunityContactRole = createOpportunityContactRole(contact, newOpportunity);
                }
                if (booleanOppMembershipOnPaper || booleanGrantRequested) {
                    if(newOpportunity != null && newOpportunity.Id != null)
                        VolunteerRegistrationUtilty.updateSiteURLAndContact('Volunteer_ThankYou' + '?ContactId='+contact.Id + '&CouncilId='+councilAccount.Id+'&CampaignMemberIds='+campaignMembers+'&OpportunityId='+newOpportunity.Id+'&FinancialAidRequired='+String.valueOf(booleanGrantRequested)+'&CashOrCheck='+String.valueOf(booleanOppMembershipOnPaper), contact);
                    else if(lifetimeOpportunity != null && lifetimeOpportunity.Id != null)
                        VolunteerRegistrationUtilty.updateSiteURLAndContact('Volunteer_ThankYou' + '?ContactId='+contact.Id + '&CouncilId='+councilAccount.Id+'&CampaignMemberIds='+campaignMembers+'&OpportunityId='+lifetimeOpportunity.Id+'&FinancialAidRequired='+String.valueOf(booleanGrantRequested)+'&CashOrCheck='+String.valueOf(booleanOppMembershipOnPaper), contact);
                        
                    Pagereference landingPage = Page.Volunteer_ThankYou;
                    if(booleanGrantRequested != null)
                        landingPage.getParameters().put('FinancialAidRequired',String.valueOf(booleanGrantRequested));
                    if(booleanOppMembershipOnPaper != null)
                        landingPage.getParameters().put('CashOrCheck',String.valueOf(booleanOppMembershipOnPaper));
                    if(contact.Id != null)
                        landingPage.getParameters().put('ContactId', contact.Id);
                    if(campaignMembers != '')
                        landingPage.getParameters().put('CampaignMemberIds',campaignMembers);
                    if(newOpportunity != null && isLifetime == false) {
                        landingPage.getParameters().put('OpportunityId',newOpportunity.Id);
                    }
                    else if(isLifetime == true && lifetimeOpportunity != null){
                        landingPage.getParameters().put('OpportunityId',lifetimeOpportunity.Id);
                    }else {
                         return addErrorMessageAndRollback(savepoint,'Membership Opportunity not created.');
                    }
                    if (councilAccount != null)
                        landingPage.getParameters().put('CouncilId', councilAccount.Id);

                    landingPage.setRedirect(true);
                    system.debug('----------landingPage---------' +landingPage);
                    return landingPage;

                }

                String siteURL = 'Volunteer_PaymentProcessing?ContactId=' + contact.Id;

                siteURL = (councilAccount != null) ? siteURL + '&CouncilId=' + councilAccount.Id : siteURL;
                siteURL = (campaignMembers != null && campaignMembers != '') ? siteURL + '&CampaignMemberIds=' + campaignMembers : siteURL;
                if(newOpportunity != null && isLifetime == false)
                    siteURL = (newOpportunity != null)? siteURL + '&OpportunityId=' + newOpportunity.Id : siteURL;
                else if(isLifetime == true && lifetimeOpportunity != null)
                    siteURL = (lifetimeOpportunity != null)? siteURL + '&OpportunityId=' + lifetimeOpportunity.Id : siteURL;
                system.debug('----------siteURL---------' +siteURL);
                
                if(contact != null && contact.Id != null)
                    VolunteerRegistrationUtilty.updateSiteURLAndContact(siteURL, contact);

                Pagereference paymentProcessingPage = Page.Volunteer_PaymentProcessing;
                
                if(contact.Id != null)
                    paymentProcessingPage.getParameters().put('ContactId', contact.Id);
                if(campaignMembers != '' && campaignMembers != null)
                    paymentProcessingPage.getParameters().put('CampaignMemberIds',campaignMembers);
                if(newOpportunity != null) {
                    paymentProcessingPage.getParameters().put('OpportunityId',newOpportunity.Id);
                }
                else if(isLifetime == true){
                    paymentProcessingPage.getParameters().put('OpportunityId',lifetimeOpportunity.Id);
                }else {
                     return addErrorMessageAndRollback(savepoint,'Membership Opportunity not created.');
                }

                if (councilAccount != null)
                    paymentProcessingPage.getParameters().put('CouncilId', councilAccount.Id);
                paymentProcessingPage.setRedirect(true);
                system.debug('----------paymentProcessingPage---------' +paymentProcessingPage);
                return paymentProcessingPage;
            }
        } catch(System.exception pException) {
            system.debug('Exception ====:  ' + pException.getMessage());
            if(pException.getMessage().contains('UNABLE_TO_LOCK_ROW')){
                if(counterUnableToLockRow < 4) {
                    Database.rollback(savepoint);
                    return submit();
                }
                else
                    return addErrorMessage('Record is locked by another user. Please re-submit the page once more.');
            }
            else
                return addErrorMessageAndRollback(savepoint, pException);
        }
        return null;
    }

    public CampaignMember[] getCampaignMembers(String contactId, Set<String> allcampaignMemberIdSet) {
        CampaignMember[] campaignMemberList = [
            Select Id
                 , Why_are_you_unsure__c
                 , Membership__c
                 , Membership_Status__c
                 , ContactId
                 , CampaignId
                 , Account__c
                 , Display_Renewal__c
                 , Active__c
              From CampaignMember
             where ContactId = :contactId
               and Id IN :allcampaignMemberIdSet
               FOR UPDATE

        ];
        system.debug('== campaignMemberList : ' + campaignMemberList);

        return (campaignMemberList != null && campaignMemberList.size() > 0) ? campaignMemberList : new List<CampaignMember>();
    }
    
    public OpportunityContactRole createOpportunityContactRole(Contact contact, Opportunity newOpportunity) {
        OpportunityContactRole opportunityContactRole = new OpportunityContactRole(
            Role = 'Adult',
            OpportunityId = newOpportunity.Id,
            ContactId =  contact.Id,
            IsPrimary = true
        );
        try {
            insert opportunityContactRole;
            return opportunityContactRole;
        } catch(Exception Ex) {
            system.debug('== Inserting opportunity contact role ===:  ' + Ex.getMessage());
        }
        return null;
    }

    public void updateCouncilAccount(Account councilAccount, Boolean booleanGrantRequested) {
        try{
            if(councilAccount != null && booleanGrantRequested != null)
                councilAccount.Volunteer_Financial_Aid_Available__c = booleanGrantRequested;
            Database.Saveresult councilSaveResult = database.update(councilAccount);
        } catch(Exception Ex) {
            //ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.WARNING, 'Council Account : ' + ex.getMessage()));
            system.debug('== Inserting opportunity contact role ===:  ' + Ex.getMessage());
        }
    }

    /*public rC_Bios__Contact_Address__c deprecated_createContactAddress(Contact contact, String addrType, String addrStreetLine1, String addrStreetLine2, String addrCity,
                                     String addrState, String addrZipCode, String addrCountry, String addrCounty) {

        rC_Bios__Contact_Address__c contactAddress = new rC_Bios__Contact_Address__c();
        List<rC_Bios__Contact_Address__c> contactAddressList = new List<rC_Bios__Contact_Address__c>();
        List<rC_Bios__Contact_Address__c> newContactAddressList = new List<rC_Bios__Contact_Address__c>();
        List<rC_Bios__Contact_Address__c> updateContactAddressList = new List<rC_Bios__Contact_Address__c>();

        if(contact != null && contact.Id != null) {
            String addressUniqueKey = '';
            rC_Bios__Address__c address = new rC_Bios__Address__c();
            rC_Bios__Address__c oldAddress = new rC_Bios__Address__c();
            List<rC_Bios__Address__c> AddressList = new List<rC_Bios__Address__c>();
            String uniqueKey = '';

            address.rC_Bios__Street_Line_1__c = (addrStreetLine1 != null && addrStreetLine1 != '') ? addrStreetLine1 : null;
            address.rC_Bios__Street_Line_2__c = (addrStreetLine2 != null && addrStreetLine2 != '') ? addrStreetLine2 : null;
            address.rC_Bios__City__c = addrCity != null ? addrCity : '';
            address.rC_Bios__State__c = addrState;
            address.rC_Bios__Postal_Code__c = addrZipCode != null && addrZipCode != '' ? addrZipCode.substring(0, 5) : null;
            address.rC_Bios__Country__c = addrCountry != null ? addrCountry : '';

            addressUniqueKey = generateUniqueMD5(address);
            system.debug('=== addressUniqueKey ===: ' + addressUniqueKey);

            if(addressUniqueKey != null && addressUniqueKey != '') {
                AddressList = [
                    Select Id, Name, rC_Bios__Unique_MD5__c
                           ,rC_Bios__County__c
                      From rC_Bios__Address__c
                     where rC_Bios__Unique_MD5__c = :addressUniqueKey
                ];
            }

            if(AddressList != null && AddressList.size() > 0) {
                oldAddress = AddressList[0];
                system.debug('oldAddress ===: ' + oldAddress);
                uniqueKey = contact.Id + '' + oldAddress.Id;
                rC_Bios__Contact_Address__c currentContactAddress = new rC_Bios__Contact_Address__c();
                rC_Bios__Contact_Address__c oldPreferredContactAddress = new rC_Bios__Contact_Address__c();

                contactAddressList = [
                    Select Id
                         , rC_Bios__Preferred_Mailing__c
                         , rC_Bios__Original_Street_Line_1__c
                         , rC_Bios__Original_Street_Line_2__c
                         , rC_Bios__Original_City__c
                         , rC_Bios__Original_State__c
                         , rC_Bios__Original_Postal_Code__c
                         , rC_Bios__Original_Country__c
                         , rC_Bios__Address__r.rC_Bios__County__c
                         , Contact_Address_UniqueKey__c
                      From rC_Bios__Contact_Address__c
                     Where rC_Bios__Contact__c =: contact.Id
                ];
                system.debug('contactAddressList ===: ' + contactAddressList.size());
                if(contactAddressList != null && contactAddressList.size() > 0){
                    for(rC_Bios__Contact_Address__c varContactAddress : contactAddressList){
                        if(varContactAddress.Contact_Address_UniqueKey__c == uniqueKey) {
                            currentContactAddress = varContactAddress;
                        }
                        if(varContactAddress.rC_Bios__Preferred_Mailing__c == true) {
                            oldPreferredContactAddress = varContactAddress;
                        }
                    }
                }
                system.debug('currentContactAddress ===: ' + currentContactAddress);
                system.debug('oldPreferredContactAddress ===: ' + oldPreferredContactAddress);

                if(currentContactAddress != null && currentContactAddress.Id != null) {
                    if(oldPreferredContactAddress != null && oldPreferredContactAddress.Id != null) {
                        if(currentContactAddress.Id == oldPreferredContactAddress.Id) {
                            if(oldAddress.rC_Bios__County__c != county) {
                                oldAddress.rC_Bios__County__c = county != null ? county : '';
                             }
                             if(currentContactAddress.rC_Bios__Preferred_Mailing__c == false){
                                 currentContactAddress.rC_Bios__Preferred_Mailing__c = true;
                                 try {
                                    update currentContactAddress;
                                 } catch(Exception Ex) {
                                    system.debug('Exception currentContactAddress ===:  ' + Ex);
                                 }
                             }
                        }
                        else {
                            currentContactAddress.rC_Bios__Preferred_Mailing__c = true;
                            oldPreferredContactAddress.rC_Bios__Preferred_Mailing__c = false;

                            updateContactAddressList.add(currentContactAddress);
                            updateContactAddressList.add(oldPreferredContactAddress);
                            if(oldAddress.rC_Bios__County__c != county) {
                                oldAddress.rC_Bios__County__c = county != null ? county : '';
                            }
                            try {
                                update updateContactAddressList;
                            } catch(Exception Ex) {
                                system.debug('Exception updateContactAddressList ===:  ' + Ex);
                            }
                        }
                     }
                 }
                 else {
                     if(oldPreferredContactAddress != null && oldPreferredContactAddress.Id != null){
                        oldPreferredContactAddress.rC_Bios__Preferred_Mailing__c = false;
                        try {
                            update oldPreferredContactAddress;
                        } catch(Exception Ex) {
                            system.debug('Exception oldPreferredContactAddress ===:  ' + Ex);
                        }
                     }

                     contactAddress.rC_Bios__Contact__c = contact.Id;
                     contactAddress.rC_Bios__Type__c = addrType != null ? addrType : '';
                     contactAddress.rC_Bios__Original_Street_Line_1__c = (addrStreetLine1 != null && addrStreetLine1 != '') ? addrStreetLine1 : null;
                     contactAddress.rC_Bios__Original_Street_Line_2__c = (addrStreetLine2 != null && addrStreetLine2 != '') ? addrStreetLine2 : null;
                     contactAddress.rC_Bios__Original_City__c = (addrCity != null && addrCity != '') ? addrCity : null;
                     contactAddress.rC_Bios__Original_State__c = (addrState != null && addrState != '') ? addrState : null;
                     contactAddress.rC_Bios__Original_Postal_Code__c = addrZipCode != null ? addrZipCode : '';
                     contactAddress.rC_Bios__Original_Country__c = addrCountry != null ? addrCountry : '';
                     contactAddress.rC_Bios__Preferred_Mailing__c = true;
                     contactAddress = VolunteerRenewalUtility.insertContactAddress(contactAddress);

                    if(oldAddress.rC_Bios__County__c != county)
                        oldAddress.rC_Bios__County__c = county != null ? county : '';
                 }
                try {
                    update oldAddress;
                }catch(Exception Ex) {
                    system.debug('Exception update oldAddress ===:  ' + Ex);
                }

            }
            else {
                rC_Bios__Contact_Address__c oldPreferredContactAddress = new rC_Bios__Contact_Address__c();
                contactAddressList = [
                    Select Id
                         , rC_Bios__Preferred_Mailing__c
                         , rC_Bios__Original_Street_Line_1__c
                         , rC_Bios__Original_Street_Line_2__c
                         , rC_Bios__Original_City__c
                         , rC_Bios__Original_State__c
                         , rC_Bios__Original_Postal_Code__c
                         , rC_Bios__Original_Country__c
                         , rC_Bios__Address__r.rC_Bios__County__c
                         , Contact_Address_UniqueKey__c
                      From rC_Bios__Contact_Address__c
                     Where rC_Bios__Contact__c =: contact.Id
                       and rC_Bios__Preferred_Mailing__c = true
                ];
                if(contactAddressList != null && contactAddressList.size()>0){
                    for(rC_Bios__Contact_Address__c oldPrefCA : contactAddressList) {
                        oldPrefCA.rC_Bios__Preferred_Mailing__c = false;
                    }
                    try {
                        update contactAddressList;
                    } catch(Exception Ex) {
                        system.debug('Exception contactAddressList ===:  ' + Ex);
                    }
                }
                contactAddress.rC_Bios__Contact__c = contact.Id;
                contactAddress.rC_Bios__Type__c = addrType != null ? addrType : '';
                contactAddress.rC_Bios__Original_Street_Line_1__c = (addrStreetLine1 != null && addrStreetLine1 != '') ? addrStreetLine1 : null;
                contactAddress.rC_Bios__Original_Street_Line_2__c = (addrStreetLine2 != null && addrStreetLine2 != '') ? addrStreetLine2 : null;
                contactAddress.rC_Bios__Original_City__c = (addrCity != null && addrCity != '') ? addrCity : null;
                contactAddress.rC_Bios__Original_State__c = (addrState != null && addrState != '') ? addrState : null;
                contactAddress.rC_Bios__Original_Postal_Code__c = addrZipCode != null ? addrZipCode : '';
                contactAddress.rC_Bios__Original_Country__c = addrCountry != null ? addrCountry : '';
                contactAddress.rC_Bios__Preferred_Mailing__c = true;
                try {
                    insert contactAddress;
                    system.debug('**********'+contactAddress);
                } catch(Exception Ex) {
                    system.debug('Contact Address Exception ===:  ' + Ex);
                }
             }
        }
        system.debug('==: Inserted child Contact Address ==: ' + contactAddress);
        return contactAddress;
    }*/
    
    public rC_Bios__Contact_Address__c createContactAddress(Contact contact, String addrType, String addrStreetLine1, String addrStreetLine2, String addrCity,
                                     String addrState, String addrZipCode, String addrCountry, String addrCounty) {

        rC_Bios__Contact_Address__c contactAddress = new rC_Bios__Contact_Address__c();
        List<rC_Bios__Contact_Address__c> contactAddressList = new List<rC_Bios__Contact_Address__c>();
        List<rC_Bios__Contact_Address__c> updateContactAddressList = new List<rC_Bios__Contact_Address__c>();
        map <String, rC_Bios__Contact_Address__c> oldContactId_UniqueMD5ToContAddress = new map <String, rC_Bios__Contact_Address__c>();
        map <Id, rC_Bios__Contact_Address__c> contactAddressMap = new map <Id, rC_Bios__Contact_Address__c>();
         
        if(contact != null && contact.Id != null) {

            String addressUniqueKey = '';
            rC_Bios__Address__c address = new rC_Bios__Address__c();

            address.rC_Bios__Street_Line_1__c = (addrStreetLine1 != null && addrStreetLine1 != '') ? addrStreetLine1 : null;
            address.rC_Bios__Street_Line_2__c = (addrStreetLine2 != null && addrStreetLine2 != '') ? addrStreetLine2 : null;
            address.rC_Bios__City__c = addrCity != null ? addrCity : '';
            address.rC_Bios__State__c = addrState;
            address.rC_Bios__Postal_Code__c = addrZipCode != null && addrZipCode != '' ? addrZipCode.substring(0, 5) : null;
            address.rC_Bios__Country__c = addrCountry != null ? addrCountry : '';

            addressUniqueKey = generateUniqueMD5(address);

            contactAddress.rC_Bios__Contact__c = contact.Id;
            contactAddress.rC_Bios__Type__c = addrType != null ? addrType : '';
            contactAddress.rC_Bios__Original_Street_Line_1__c = (addrStreetLine1 != null && addrStreetLine1 != '') ? addrStreetLine1 : null;
            contactAddress.rC_Bios__Original_Street_Line_2__c = (addrStreetLine2 != null && addrStreetLine2 != '') ? addrStreetLine2 : null;
            contactAddress.rC_Bios__Original_City__c = (addrCity != null && addrCity != '') ? addrCity : null;
            contactAddress.rC_Bios__Original_State__c = addrState;
            contactAddress.rC_Bios__Original_Postal_Code__c = addrZipCode != null ? addrZipCode : null;
            contactAddress.rC_Bios__Original_Country__c = (addrCountry != null && addrCountry != '') ? addrCountry : null;
            contactAddress.rC_Bios__Preferred_Mailing__c = true;

            String ContactId_UniqueMD5 = contact.Id + addressUniqueKey;

            contactAddressList = [
                Select Id
                     , rC_Bios__Address__r.rC_Bios__Unique_MD5__c
                     , rC_Bios__Preferred_Mailing__c
                     , Contact_Address_UniqueKey__c
                     , rC_Bios__Contact__c
                  From rC_Bios__Contact_Address__c
                 Where rC_Bios__Contact__c = :contact.Id
            ];

            if(contactAddressList != null && contactAddressList.size() > 0){
                for(rC_Bios__Contact_Address__c oldContactAddress : contactAddressList){
                    if(oldContactAddress != null){
                        String oldContactId_UniqueMD5 = oldContactAddress.rC_Bios__Contact__c + oldContactAddress.rC_Bios__Address__r.rC_Bios__Unique_MD5__c;
                        oldContactId_UniqueMD5ToContAddress.put(oldContactId_UniqueMD5, oldContactAddress);
                    }
                }
            }

            try {
                if(oldContactId_UniqueMD5ToContAddress.size() > 0) {
                    if(oldContactId_UniqueMD5ToContAddress.containsKey(ContactId_UniqueMD5)) {
                        rC_Bios__Contact_Address__c existingContactAddress = oldContactId_UniqueMD5ToContAddress.get(ContactId_UniqueMD5);
                        if(existingContactAddress != null && existingContactAddress.rC_Bios__Preferred_Mailing__c == true){
                            //Do nothing
                            system.debug('-------Do nothing--------');
                        }
                        else{
                            for(rC_Bios__Contact_Address__c oldContactAddress : oldContactId_UniqueMD5ToContAddress.values()){
                                if(oldContactAddress.rC_Bios__Preferred_Mailing__c == true){
                                    oldContactAddress.rC_Bios__Preferred_Mailing__c = false;
                                    contactAddressMap.put(oldContactAddress.Id, oldContactAddress);
                                }
                            }
                            existingContactAddress.rC_Bios__Preferred_Mailing__c = true;
                            contactAddressMap.put(existingContactAddress.Id, existingContactAddress);
                            contactAddress = existingContactAddress;
                        }
                        if(contactAddressMap.size() > 0)
                            update contactAddressMap.values();                            
                    }
                    else {
                        for(rC_Bios__Contact_Address__c oldContactAddress : oldContactId_UniqueMD5ToContAddress.values()){
                            if(oldContactAddress.rC_Bios__Preferred_Mailing__c == true){
                                oldContactAddress.rC_Bios__Preferred_Mailing__c = false;
                                contactAddressMap.put(oldContactAddress.Id, oldContactAddress);
                            }
                        }
                        if(contactAddressMap.size() > 0)
                            update contactAddressMap.values();

                        insert contactAddress;
                    }
                    
                }
                else{
                    insert contactAddress;
                }
            } catch(Exception Ex) {
                system.debug('== Address Exception :====>  ' + ex.getMessage());
            }
        }
        return contactAddress;
    }

    public static String generateUniqueMD5(rC_Bios__Address__c address) {
        String base = ':' + address.rC_Bios__Street_Line_1__c
                    + ':' + address.rC_Bios__Street_Line_2__c
                    + ':' + address.rC_Bios__City__c
                    + ':' + address.rC_Bios__State__c
                    + ':' + address.rC_Bios__Postal_Code__c
                    + ':' + address.rC_Bios__Country__c;
        return EncodingUtil.convertToHex(Crypto.generateDigest('MD5', Blob.valueOf(base.toLowerCase())));
    }

    public void updateAddressOwner(List<rC_Bios__Contact_Address__c> ContactAddressList) {

        set<Id> addressIdSet = new set<Id>();
        set<String> zipCodeSet = new set<String>();

        map<rC_Bios__Address__c, Id> addressToOwnerIdMap = new map<rC_Bios__Address__c, Id>();
        Set<rC_Bios__Address__c> addressToUpdateSet = new set<rC_Bios__Address__c>();
        List<rC_Bios__Address__c> addressToUpdateList = new List<rC_Bios__Address__c>();

        map<String, rC_Bios__Contact_Address__c> mailingPostalCodeToContactAddressMap = new map<String, rC_Bios__Contact_Address__c>();
        map<String, Zip_Code__c> mailingPostalCodeToZipCodeMap = new map<String, Zip_Code__c>();

        if(ContactAddressList != null && ContactAddressList.size() > 0) {
            for(rC_Bios__Contact_Address__c contactAddress : ContactAddressList) {
                if(contactAddress != null){
                    if(contactAddress.rC_Bios__Address__c != null)
                        addressIdSet.add(contactAddress.rC_Bios__Address__c);

                    if(contactAddress.rC_Bios__Contact__r.MailingPostalCode != null && contactAddress.rC_Bios__Contact__r.MailingPostalCode != ''){
                        zipCodeSet.add(contactAddress.rC_Bios__Contact__r.MailingPostalCode.substring(0, 5));
                        mailingPostalCodeToContactAddressMap.put(contactAddress.rC_Bios__Contact__r.MailingPostalCode.substring(0, 5), contactAddress);
                    }
                }
            }
        }

        map<Id, rC_Bios__Address__c> addressMap = new map<Id, rC_Bios__Address__c>([Select Id, OwnerId From rC_Bios__Address__c where Id IN : addressIdSet FOR UPDATE]);

        Zip_Code__c[] zipCodeList = [
            Select Id
                 , Name
                 , Council__c
                 , Zip_Code_Unique__c
                 , City__c
                 , Recruiter__c
                 , Recruiter__r.IsActive
                 , Recruiter__r.UserRoleId
              From Zip_Code__c
             where Zip_Code_Unique__c IN :zipCodeSet
        ];

        if(zipCodeList.size() > 0) {
            for(Zip_Code__c zipCode : zipCodeList) {
                if(zipCode != null && zipCode.Zip_Code_Unique__c != null)
                    mailingPostalCodeToZipCodeMap.put(zipCode.Zip_Code_Unique__c.substring(0, 5), zipCode);
            }
        }

        if(ContactAddressList != null && ContactAddressList.size() > 0) {

            for(rC_Bios__Contact_Address__c contactAddress : ContactAddressList) {

                rC_Bios__Address__c addressToUpdate = new rC_Bios__Address__c();

                if(contactAddress != null && contactAddress.rC_Bios__Contact__r.MailingPostalCode != null && mailingPostalCodeToContactAddressMap.containsKey(contactAddress.rC_Bios__Contact__r.MailingPostalCode)) {

                    if(mailingPostalCodeToZipCodeMap.containsKey(contactAddress.rC_Bios__Contact__r.MailingPostalCode)) {

                        Zip_Code__c matchedZipCode = mailingPostalCodeToZipCodeMap.get(contactAddress.rC_Bios__Contact__r.MailingPostalCode);

                        addressToUpdate = addressMap.get(contactAddress.rC_Bios__Address__c);

                        if(matchedZipCode.Recruiter__c != null && matchedZipCode.Recruiter__r.IsActive && matchedZipCode.Recruiter__r.UserRoleId != null && contactAddress.rC_Bios__Address__c != null)
                            addressToUpdate.OwnerId = matchedZipCode.Recruiter__c;
                        else if(defaultSystemAdminUser != null && defaultSystemAdminUser.Id != null)
                            addressToUpdate.OwnerId = defaultSystemAdminUser.Id;
                    }
                }
                if(county != null)
                    addressToUpdate.rC_Bios__County__c = county;
                if(addressToUpdate != null)
                    addressToUpdateSet.add(addressToUpdate);
            }
        }

        if(addressToUpdateSet != null && addressToUpdateSet.size() > 0) {
            try {
                addressToUpdateList.clear();
                addressToUpdateList.addAll(addressToUpdateSet);
                if(addressToUpdateList != null && addressToUpdateList.size() > 0)
                update addressToUpdateList;

            } catch(Exception Ex) {
                system.debug('Exception :===>  ' + Ex.getMessage());
            }
        }
    }

    public void updateOpportunityType(Opportunity newOpportunity, PricebookEntry priceBookEntry, Contact contact) {

        if (newOpportunity != null && priceBookEntry != null && contact.Id != null && contact != null) {

            newOpportunity.Type = ( priceBookEntry.Name.toUpperCase().contains('ADULT'))
                                    ? 'Adult Membership'
                                    : (     priceBookEntry.Name.toUpperCase().contains('LIFETIME')
                                            ? 'Lifetime Membership'
                                            : newOpportunity.Type );

            newOpportunity.Contact__c = contact.Id;
            newOpportunity.Membership_Status__c = (contact.Ineligible__c == true) ? 'Ineligible' : 'Payment Pending';
            newOpportunity.rC_Giving__Primary_Contact__c = contact.Id;
           // newOpportunity.Membership_on_Paper__c = (booleanOppMembershipOnPaper == true) ? true : false;
            newOpportunity.Adult_Email__c = (preferredEmail != null && preferredEmail != ''
                                            && preferredEmail.equalsIgnoreCase('Email2') && email2 <> '') ? email2 : '' ;
            try{
                if(newOpportunity != null)
                    update newOpportunity;
            } catch(Exception Ex) {
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.WARNING, 'Update Opportunity : ' + ex.getMessage()));
            }
        }
    }

    public OpportunityLineItem createOpportunityLineItem(PricebookEntry priceBookEntry, Opportunity newOpportunity) {
        if(priceBookEntry != null && priceBookEntry.Id != null && newOpportunity != null && newOpportunity.Id != null){
            OpportunityLineItem opportunityLineItem = new OpportunityLineItem();
            opportunityLineItem.PricebookEntryId = priceBookEntry.Id;
            opportunityLineItem.OpportunityId = newOpportunity.Id;
            opportunityLineItem.Quantity = 1;

            if (priceBookEntry.UnitPrice != null)
                opportunityLineItem.UnitPrice = priceBookEntry.UnitPrice;

            try{
                insert opportunityLineItem;
                /** Move code for update transaction opportunity Start. Moved by Chandra**/
                List<Opportunity> transactionOppList = [
                        Select rC_Giving__Parent__c
                             , Name
                             , OwnerId
                             , RecordType.Name
                             , RecordType.Id
                             , RecordTypeId
                          From Opportunity
                         where RecordType.Name = 'Transaction'
                           and rC_Giving__Parent__c = :newOpportunity.Id
                           FOR UPDATE
                    ];
                     system.debug('***transactionOppList***'+transactionOppList);
                    if(transactionOppList.size() > 0) {
                        for(Opportunity transactionOpportunity : transactionOppList) {
                            transactionOpportunity.OwnerId = newOpportunity.OwnerId;
                        }
                        update transactionOppList;
                    }
                     system.debug('***transactionOppListAfterUpdate***'+transactionOppList);
                  /** Move code for update transaction opportunity End. Moved by Chandra**/
            } catch(Exception Ex) {
                system.debug('=== Ex : ' + Ex);
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.WARNING, 'Line Item : ' + ex.getMessage()));
                return null;
            }
            return (opportunityLineItem != null) ? opportunityLineItem : null;
        }
        return null;
    }

    public Opportunity createMembershipOpportunity(String recordTypeId, Contact contact, PricebookEntry priceBookEntry) {

        String campaignName = '';
        String membershipYear = string.valueOf(system.today().year());
        system.debug('***start****');
        if(priceBookEntry != null && priceBookEntry.Product2.Name.toUpperCase().contains('LIFETIME')) {
            system.debug('***start123****');
            campaignName = LIFETIME_MEMBERSHIP;
        }
        else if(priceBookEntry != null && priceBookEntry.Product2.rC_Giving__End_Date__c != null) {
            system.debug('***start12345****');
            membershipYear = string.valueOf(priceBookEntry.Product2.rC_Giving__End_Date__c.year());
            campaignName = membershipYear + ' Membership';
            system.debug('***campaignName****'+campaignName);
        }

        if(contact != null && contact.AccountId != null && campaignName != null && campaignName != '') {
            Account[] accountList = [
                Select Id
                  from Account
                 where Id = :contact.AccountId
                 limit 1
            ];
            system.debug('***accountList***'+accountList);
            Campaign[] membershipCampaignList = [
                Select Id
                     , Name
                  From Campaign
                 where Name = :campaignName
                 limit 1
            ];
            system.debug('***membershipCampaignList***'+membershipCampaignList);
            Opportunity opportunity;

            if (accountList != null && accountList.size() > 0 && membershipCampaignList != null && membershipCampaignList.size() >0) {
                opportunity = new Opportunity(
                    RecordTypeId = recordTypeId,
                    AccountId = accountList[0].Id,
                    CampaignId = membershipCampaignList[0].Id,
                    rC_Giving__Giving_Amount__c = (priceBookEntry.UnitPrice != null) ? priceBookEntry.UnitPrice : 0,
                    StageName = 'Open',
                    CloseDate = System.today(),
                    Membership_Year__c = membershipYear,
                    Membership_on_Paper__c = (booleanOppMembershipOnPaper == true) ? true : false, //Added by chandra
                    Grant_Requested__c     = (booleanGrantRequested == true) ? true : false, //Added by chandra
                   rC_Giving__Is_Giving__c = true
                );
                system.debug('***opportunity***'+opportunity);
                if(matchingZipCode.Recruiter__c != null && matchingZipCode.Recruiter__r.IsActive && matchingZipCode.Recruiter__r.UserRoleId != null)
                    opportunity.OwnerId = matchingZipCode.Recruiter__c;
                else if(defaultSystemAdminUser != null && defaultSystemAdminUser.Id != null)
                    opportunity.OwnerId = defaultSystemAdminUser.Id;
                system.debug('***opportunity11111***'+opportunity);
                try{
                    insert opportunity;
                    system.debug('***opportunityInderted***'+opportunity);
                    /** Move code for update transaction opportunity From Here to Line no 835. Moved by Chandra**/
                } catch(Exception pException) {
                    system.debug('=== pException : ' + pException);
                    throw pException;
                    //ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.WARNING, 'Opportunity : ' + ex.getMessage()));
                    return null;
                }
            }

            return opportunity;
        }
        return null;
    }

    public void updateCampaignMembers(CampaignMember[] campaignMemberList, Opportunity newOpportunity) {
        if (campaignMemberList != null && campaignMemberList.size() > 0 && newOpportunity != null && newOpportunity.Id != null) {
             system.debug('***1111***');
            for(CampaignMember campaignMember : campaignMemberList){
                if(campaignMember != null && newOpportunity != null && newOpportunity.Id != null)
                    campaignMember.Membership__c = newOpportunity.Id;
            }
            system.debug('***2222***');
            try{
                if(campaignMemberList != null && campaignMemberList.size() > 0)
                    update campaignMemberList;
                    system.debug('******'+campaignMemberList);
            } catch(Exception Ex) {
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.WARNING, 'Update Campaign Member : ' + ex.getMessage()));
            }
        }
    }

    public Contact updateContact(Contact contact) {
        Contact contactToUpdate;
        if (contact != null && contact.Id != null)
            contactToUpdate = contact;
        if (contactToUpdate != null) {
            String[] strBirthDate = (dateOfBirth != null && dateOfBirth != '') ? dateOfBirth.trim().split('/') : new String[]{};

            if(strBirthDate != null && strBirthDate.size() > 0) {
                contactToUpdate.rC_Bios__Birth_Day__c=strBirthDate[1].trim();
                contactToUpdate.rC_Bios__Birth_Month__c=strBirthDate[0].trim();
                contactToUpdate.rC_Bios__Birth_Year__c=strBirthDate[2].trim();
            }
            contactToUpdate.FirstName = firstName != null ? firstName : '';
            contactToUpdate.LastName = lastName != null ? lastName : '';

            if(gender != null && gender != '')
            contactToUpdate.rC_Bios__Gender__c = gender.toUpperCase().contains('NONE') ? '' : gender;

            contactToUpdate.rC_Bios__Home_Email__c = email != null ? email : '';
            contactToUpdate.rC_Bios__Work_Email__c = (email2 != null && email2 != '') ? email2 : '';
            contactToUpdate.rC_Bios__Minor_Child__c = false;

            if(preferredEmail.equalsIgnoreCase('Email'))
                contactToUpdate.rC_Bios__Preferred_Email__c = 'Home';

            if(preferredEmail.equalsIgnoreCase('Email2'))
                contactToUpdate.rC_Bios__Preferred_Email__c = 'Work';

            if(preferredPhone != null && preferredPhone.equalsIgnoreCase('Home Phone'))
                contactToUpdate.rC_Bios__Preferred_Phone__c = 'Home';
            else if(preferredPhone != null && preferredPhone.equalsIgnoreCase('Business Phone'))
                contactToUpdate.rC_Bios__Preferred_Phone__c = 'Work';
            else if(preferredPhone != null && preferredPhone.equalsIgnoreCase('Mobile Phone'))
                contactToUpdate.rC_Bios__Preferred_Phone__c = 'Mobile';

            contactToUpdate.HomePhone = homePhone != null ? homePhone : '';
            contactToUpdate.rC_Bios__Work_Phone__c = businessPhone != null ? businessPhone : '';
            contactToUpdate.MobilePhone = mobilePhone != null ? mobilePhone : '';

            contactToUpdate.Email_Opt_In__c = booleanContactEmailOptIn;
            contactToUpdate.Text_Phone_Opt_In__c = booleanContactTextPhoneOptIn;
            contactToUpdate.Photo_Opt_In__c = booleanContactPhotoOptIn;
            contactToUpdate.Volunteer_Terms_and_Conditions__c = booleanTermsAndConditions;

            try{
                update contactToUpdate;
                return contactToUpdate;
            }catch(Exception Ex) {
                        if(string.valueOf(Ex).contains('The Birthdate cannot be in the future')) {
                            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.WARNING, 'The Birthdate cannot be in the future.'));
                        }else {
                            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.WARNING, 'Contact Update Failed'));
                        }
                }

        }
        return null;
    }

    public Contact getContact(String contactId) {
        if(contactId != null && contactId != ''){
            Contact[] contactList = [
                Select Id
                     , FirstName
                     , LastName
                     , Birthdate
                     , AccountId
                     , rC_Bios__Home_Email__c
                     , rC_Bios__Gender__c
                     , rC_Bios__Preferred_Email__c
                     , MailingPostalCode
                     , HomePhone
                     , Adult_Member__c
                  From Contact
                 where Id = :contactId limit 1
            ];

            return (contactList != null && contactList.size() > 0) ? contactList[0] : new Contact();
        }
        return null;
    }

    public CampaignMember[] getCampaignMember(String contactId, Set<String> campaignMemberIdSet) {
        if(contactId != null && contactId != '' && campaignMemberIdSet != null && campaignMemberIdSet.size() > 0){
            CampaignMember[] campaignMemberList = [
                Select Id
                     , Why_are_you_unsure__c
                     , Membership__c
                     , Membership_Status__c
                     , ContactId
                     , CampaignId
                     , Account__c
                  From CampaignMember
                 where ContactId = :contactId
                   and Id IN :campaignMemberIdSet
                   FOR UPDATE
            ];
            return (campaignMemberList != null && campaignMemberList.size() > 0) ? campaignMemberList : new List<CampaignMember>();
        }
        return null;
    }

    public List<SelectOption> getlistCountryItems() {
        List<SelectOption> countryOptions = new List<SelectOption>();
        map<String, CountryNames__c> countryNamesMap = CountryNames__c.getAll();

        //countryOptions.add(new Selectoption('--None--', '--None--'));
        if(!countryNamesMap.isEmpty()){
            for(String countryName : countryNamesMap.keySet())
                countryOptions.add(new Selectoption(countryName, countryName));
        }
        return countryOptions;
    }

     public List<SelectOption> getlistStateItems() {

        List<StateNames__c> stateNamesList = StateNames__c.getAll().values();
        List<String> stateNamesSortedList = new List<String>();
        List<SelectOption> stateOptions = new List<SelectOption>();

        for(StateNames__c stateName : stateNamesList){
            if(stateName != null && stateName.Name != null)
                stateNamesSortedList.add(stateName.Name);
        }

        stateNamesSortedList.sort();
        stateOptions.add(new SelectOption('--None--', '--None--'));

        for(String stateName : stateNamesSortedList){
            if(stateName != null)
                stateOptions.add(new SelectOption(stateName, stateName));
        }
        return stateOptions;
    }

    @future
    public static void updateCouncilAccountsFinancialAid (Id councilAccountId, boolean booleanGrantRequested) {
        if(councilAccountId != null && booleanGrantRequested != null) {
            List<Account> accountList = [Select Volunteer_Financial_Aid_Available__c, Id From Account where Id = :councilAccountId];
            Account councilAccount1 = (accountList != null && accountList.size() > 0) ? accountList[0] : new Account();

            if(councilAccount1 != null && councilAccount1.Id != null) {
                councilAccount1.Volunteer_Financial_Aid_Available__c = booleanGrantRequested;
                update councilAccount1;
            }
        }
    }

}