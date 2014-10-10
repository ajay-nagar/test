public class VolunteerRenewal_MemberInfoController extends SobjectExtension{

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

    //public Boolean firstCheked {get; set;}
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
    public Boolean isLifetime { get; set; }

    public PricebookEntry[] PricebookEntryList;
    public map<Id, PricebookEntry> priceBookEntryMap;

    public Set<Id> oldOpportunityIdSet;
    public Set<String> campaignMemberIdSet;
    public Contact contact;
    public Opportunity oldOpportunity;
    public Opportunity newOpportunity;
    public List<Opportunity> memberOpportunityList;
    public Opportunity lifetimeOpportunity;

    public static final String MEMBERSHIP_RECORDTYPE = 'Membership';
    public static final string GIRLSCOUTSUSA = 'Girl Scouts USA';
    public static final string MOBILE_PHONE = 'Mobile Phone';
    public static final string BUSINESS_PHONE = 'Business Phone';
    public static final string HOME_PHONE = 'Home Phone';
    public static final string LIFETIME_MEMBERSHIP = 'Lifetime Membership';

    private static Integer counterUnableToLockRow = 0;
    private User defaultSystemAdminUser = new User();
    private Zip_Code__c matchingZipCode = new Zip_Code__c();

    private static final map<String, Schema.RecordTypeInfo> OPPORTUNITY_RECORDTYPE_INFO_MAP =  Opportunity.SObjectType.getDescribe().getRecordTypeInfosByName();

    private static String getOppRecordTypeId(String name) {
       return (OPPORTUNITY_RECORDTYPE_INFO_MAP.get(name) != null) ? OPPORTUNITY_RECORDTYPE_INFO_MAP.get(name).getRecordTypeId() : null;
    }

    public List<SelectOption> getmembershipProductList() {

        List<SelectOption> membershipProducts = new List<SelectOption>();
        Integer Year = Date.Today().Year() + 1;
        String sYear = String.valueOf(Year);
        Integer currentYear = Date.Today().Year();
        String currentYearStr = String.valueOf(currentYear);

        /*
        for(PricebookEntry pricebookEntry : PricebookEntryList) {
            if (pricebookEntry.Name.toUpperCase().contains('ADULT') && pricebookEntry.Name.toUpperCase().contains(sYear))
                membershipProducts.add(new SelectOption(pricebookEntry.Id, pricebookEntry.Name));
        }

        for(PricebookEntry pricebookEntry : PricebookEntryList) {
            if ((pricebookEntry.Name.toUpperCase().contains('ADULT') || pricebookEntry.Name.toUpperCase().contains('LIFETIME')) && (!pricebookEntry.Name.toUpperCase().contains(sYear)) && (!pricebookEntry.Name.toUpperCase().contains(currentYearStr))) {
                if(isLifetime) {
                    if(pricebookEntry.Name.toUpperCase().contains('LIFETIME') && lifetimeOpportunity.Type.toUpperCase().contains('LIFETIME'))
                        membershipProducts.add(new SelectOption(pricebookEntry.Id, pricebookEntry.Name));
                }
                else
                    membershipProducts.add(new SelectOption(pricebookEntry.Id, pricebookEntry.Name));
            }
        }
        */

        //for lifetime membership
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
        } else {
            for(PricebookEntry pricebookEntry : PricebookEntryList) {
                if (pricebookEntry.Name.toUpperCase().contains('ADULT') && pricebookEntry.Name.toUpperCase().contains(sYear))
                    membershipProducts.add(new SelectOption(pricebookEntry.Id, '$'+pricebookEntry.UnitPrice +' ' +pricebookEntry.Name));
            }

            for(PricebookEntry pricebookEntry : PricebookEntryList) {
                if ((pricebookEntry.Name.toUpperCase().contains('ADULT') || pricebookEntry.Name.toUpperCase().contains('LIFETIME')) && (!pricebookEntry.Name.toUpperCase().contains(sYear)) && (!pricebookEntry.Name.toUpperCase().contains(currentYearStr))) {
                    membershipProducts.add(new SelectOption(pricebookEntry.Id, '$'+pricebookEntry.UnitPrice +' ' +pricebookEntry.Name));
                }
            }
        }

        system.debug('=membershipProducts====>'+membershipProducts);
        return membershipProducts;
    }
    private String membershipYear = '';
    private string oldCampaign = '';
    public VolunteerRenewal_MemberInfoController() {
        counterUnableToLockRow = 0;
        tremsflag = false;
        booleanGrantRequested = false;
        booleanContactPhotoOptIn = true;
        readAndAgree = 'value that specifies whether this checkbox 10.0 global should ';

        term1 = 'I wish to opt out at this time. I will abide by the Girl Scout Promise and Law.';
        term2 = 'I will follow all Girl Scouts (Council Name Here) policies in my role as a volunteer.';
        term3 = 'I agree to complete a background check in order to help ensure the safety of the youth served through Girl Scouts.';
        term4 = 'I will respect and maintain the confindentiality of all previleged information related to Girls Scouts, its girls, its Volunteers or staff to which I am exposed while serving as a volunteer.';

        termsAndCondition = 'I accept and abide by the Girl Scouts Promise and Law.';
        memberShipOnPaper = 'If you prefer not to pay by credit card, click here to pay by cash/check.';
        grantRequested = 'I request financial assistance for membership registration';
        thankYou = 'Value that specifies whether this checkbox 10.0 global should /n 2.value that specifies whether this checkbox 10.0 global should';

        

        priceBookEntryMap = new map<Id, PricebookEntry>();
        PricebookEntryList = new PricebookEntry[]{};

        rC_Bios__Address__c address = new rC_Bios__Address__c();
        Zip_Code__c zipCodeFromContactPostalCode;

        fillPricebookEntryList();

        oldOpportunityIdSet = new set<Id>();

        String oldOpportunityId = Apexpages.currentPage().getParameters().get('oldOpportunityIds');
        String contactId = Apexpages.currentPage().getParameters().get('ContactId');
        String councilId = Apexpages.currentPage().getParameters().get('CouncilId');

        if (contactId != null && contactId != '') {
            contact = VolunteerRenewalUtility.getContact(contactId);

            if (councilId != null && councilId != '') {
                councilAccount = VolunteerRenewalUtility.getCouncilAccount(councilId);
                VolunteerController.councilAccount = councilAccount;
            } else if(contact != null && contact.MailingPostalCode != null && contact.MailingPostalCode != '') {
                zipCodeFromContactPostalCode = VolunteerRenewalUtility.getZipCode(contact.MailingPostalCode);
                councilAccount = VolunteerRenewalUtility.getCouncilAccount(zipCodeFromContactPostalCode.Council__c);
                VolunteerController.councilAccount = councilAccount;
            }
            defaultSystemAdminUser = [Select Id
                 , LastName
                 , IsActive
                 , Profile.Name
                 , Profile.Id
                 , ProfileId  from User where Id = :contact.Account.OwnerId
               and IsActive = true 
               and UserRoleId != null
            limit 1];
        }

        if (councilAccount != null) {
            if (councilAccount.Terms_Conditions__c != null)
                tremsflag = true;
        }

        isCouncilTermsConditionsAvailable = (councilAccount != null && councilAccount.Terms_Conditions__c != null && councilAccount.Terms_Conditions__c != '')
                                            ? true
                                            : false;

        financialCheckBox  = (councilAccount != null && councilAccount.Volunteer_Financial_Aid_Available__c != null && councilAccount.Volunteer_Financial_Aid_Available__c == true)
                             ? true
                             : false;

        /*if (oldOpportunityId != null && oldOpportunityId != ''){
            oldOpportunity = getOldOpportunity(oldOpportunityId);
        }*/

        if (contactId != null && contactId != '') {

            rC_Bios__Contact_Address__c contactAddress = new  rC_Bios__Contact_Address__c();
            rC_Bios__Contact_Address__c[] contactAddressList = [
                Select Id
                     , rC_Bios__Contact__c
                     , rC_Bios__Address__c
                  From rC_Bios__Contact_Address__c
                 where rC_Bios__Contact__c =: contactId
                   And rC_Bios__Preferred_Mailing__c = true
                 limit 1
            ];
            contactAddress = (contactAddressList != null && contactAddressList.size() > 0) ? contactAddressList[0] : null;
            if(contactAddress != null)
                address = getAddress(contactAddress.rC_Bios__Address__c);
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
        if (contact != null) {
            //Date contactBirthDate = contact.Birthdate
            String sDate = contact.Birthdate != null ? String.valueOf(contact.Birthdate.month()) + '/' + String.valueOf(contact.Birthdate.day() + '/' + String.valueOf(contact.Birthdate.year())) : '';

            firstName = contact.FirstName;
            lastName = contact.LastName;
            dateOfBirth = sDate;
            email = contact.rC_Bios__Home_Email__c;
            email2 = contact.rC_Bios__Work_Email__c;

            String preferredEmailString = '';
            if(contact.rC_Bios__Preferred_Email__c != null && contact.rC_Bios__Preferred_Email__c.equalsIgnoreCase('Home'))
                preferredEmailString = 'Email';
            else if(contact.rC_Bios__Preferred_Email__c != null && contact.rC_Bios__Preferred_Email__c.equalsIgnoreCase('Work'))
                preferredEmailString = 'Email2';

            preferredEmail = preferredEmailString;
            homePhone = contact.HomePhone;
            businessPhone = contact.rC_Bios__Work_Phone__c;
            mobilePhone = contact.MobilePhone;

            if(contact.rC_Bios__Preferred_Phone__c != null && contact.rC_Bios__Preferred_Phone__c.toUpperCase().contains('HOME'))
                preferredPhone = HOME_PHONE;
            else if(contact.rC_Bios__Preferred_Phone__c != null && contact.rC_Bios__Preferred_Phone__c.toUpperCase().contains('BUSINESS'))
                preferredPhone = BUSINESS_PHONE;
            else if(contact.rC_Bios__Preferred_Phone__c != null && contact.rC_Bios__Preferred_Phone__c.toUpperCase().contains('MOBILE'))
                preferredPhone = MOBILE_PHONE;

            streetLine1 = address.rC_Bios__Street_Line_1__c;
            streetLine2 = address.rC_Bios__Street_Line_2__c;
            county = address.rC_Bios__County__c;
            city = address.rC_Bios__City__c;

            //state = VolunteerRegistrationUtilty.getStateName(address.rC_Bios__State__c);
            state = address.rC_Bios__State__c;
            country = address.rC_Bios__Country__c;

            zipCode =   (address != null && address.rC_Bios__Postal_Code__c != null)
                        ? ((address.rC_Bios__Postal_Code__c.length() > 5) ? address.rC_Bios__Postal_Code__c.substring(0, 5) : address.rC_Bios__Postal_Code__c)
                        : contact.MailingPostalCode;
        }

        system.debug('==contactId :  ' + contactId);
    }

    public Opportunity getOldOpportunity(){
        List<Opportunity> opportunityList = new List<Opportunity>();
        opportunityList = [
            Select Id
                 , Background_Check__c
              From Opportunity
             Where Id IN : oldOpportunityIdSet
        ];
        system.debug('==opportunityList :  ' + opportunityList);
        return (opportunityList != null && opportunityList.size() > 0) ? opportunityList[0] : null;
    }

    public rC_Bios__Address__c getAddress(String addressId) {
        rC_Bios__Address__c[] AddressList = [
            Select Id
                 , rC_Bios__Street_Line_1__c
                 , rC_Bios__Street_Line_2__c
                 , rC_Bios__City__c
                 , rC_Bios__State__c
                 , rC_Bios__ZIP__c
                 , rC_Bios__County__c
                 , rC_Bios__Country__c
                 , rC_Bios__Postal_Code__c
              From rC_Bios__Address__c
             where Id =: addressId
             limit 1
             FOR UPDATE
        ];
        system.debug('AddressList===>'+AddressList);
        return (AddressList != null && AddressList.size() > 0) ? AddressList[0] : null;
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
                 , Pricebook2Id
                 , Pricebook2.Id
                 , Pricebook2.Name
                 , Pricebook2.IsActive
                 , Pricebook2.Description
                 , Product2Id
                 , Product2.Name
                 , Product2.rC_Giving__Start_Date__c
                 , Product2.rC_Giving__End_Date__c
              From PricebookEntry
             where Pricebook2.Name = 'Girl Scouts USA'
               and Pricebook2.IsActive = true
               and IsActive = true
        ];

        if (PricebookEntryList != null && PricebookEntryList.size() >0) {
            for(PricebookEntry pricebookEntry : PricebookEntryList) {
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
        String campaignMemberIds;
        counterUnableToLockRow++;
        Savepoint savepoint = Database.setSavepoint();
        if(preferredEmail.equalsIgnoreCase('Email2') && email2 == '') {
             return addErrorMessage('Email 2 cannot be left blank when selecting Email 2 as Preferred.');
        }
        try {
            newOpportunity = new Opportunity();
            OpportunityLineItem opportunityLineItem;
            PricebookEntry priceBookEntry;
            Account account;
            CampaignMember[] campaignMembersList;
            CampaignMember[] renewCampaignMembersList;
            set<Id> ContactAddressIdSet = new set<Id>();

            if(booleanTermsAndConditions == false) {
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.WARNING, 'To Proceed, You must agree to accept and abide by the Girl Scout Promise and Law.'));
                return null;
            }

            campaignMemberIdSet = new Set<String>();

            String campaignMembers;

            if (Apexpages.currentPage().getParameters().containsKey('CampaignMemberIds')) {
                campaignMembers = Apexpages.currentPage().getParameters().get('CampaignMemberIds');
            }

            system.debug('membershipProduct ===:' + membershipProduct + '|');
            if (priceBookEntryMap != null && priceBookEntryMap.size() > 0) {
               if ( membershipProduct != null && !membershipProduct.toUpperCase().contains('NONE') && priceBookEntryMap.containsKey(membershipProduct.trim()))
                   priceBookEntry = priceBookEntryMap.get(membershipProduct.trim());
            }
            system.debug('priceBookEntry ===: ' + priceBookEntry);

            String[] campaignMemberIdList;

            if (campaignMembers != null && campaignMembers != '') {
                campaignMemberIdList = campaignMembers.trim().split(',');
                oldCampaign = campaignMemberIdList[0];
                for(String campaignMember : campaignMemberIdList)
                    campaignMemberIdSet.add(campaignMember.trim());
            }

            Zip_Code__c[] zipCodeList = new Zip_Code__c[] {};
            String zipCodeToMatch = (zipCode != null && zipCode.length() > 5) ? zipCode.substring(0, 5) + '%' : zipCode + '%';
            if(zipCodeToMatch != null && zipCodeToMatch != '') {
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
                     where Zip_Code_Unique__c like :zipCodeToMatch limit 1
                ];
            }

            matchingZipCode = (zipCodeList.size() > 0) ? zipCodeList[0] : new Zip_Code__c();

            contact = updateContact(contact);
            //rC_Bios__Contact_Address__c contactAddress = populateContactAddress(contact, 'Home', streetLine1, streetLine2, city, state, zipCode, country, null);
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
            updateAddressOwner(contactAddressList);
            if(isLifetime == false){
                newOpportunity = createMembershipOpportunity(getOppRecordTypeId(MEMBERSHIP_RECORDTYPE), contact, oldOpportunity, priceBookEntry);
                if(newOpportunity == null) {
                    return addErrorMessageAndRollback(savepoint, 'Membership Opportunity not created.');
                }

                if (priceBookEntryMap != null && priceBookEntryMap.size() > 0) {
                    if ( !membershipProduct.toUpperCase().contains('NONE') && priceBookEntryMap.containsKey(membershipProduct.trim()))
                        priceBookEntry = priceBookEntryMap.get(membershipProduct.trim());
                }

                if (contact == null || priceBookEntry == null) {
                    return addErrorMessageAndRollback(savepoint,'No results match the search criteria.');
                }

                campaignMembersList = getCampaignMembers(contact.Id, campaignMemberIdSet);

                if(campaignMembersList != null && campaignMembersList.size() > 0){
                    for(CampaignMember campaignMember : campaignMembersList){
                        if(campaignMember.Membership__c != null)
                            oldOpportunityIdSet.add(campaignMember.Membership__c);
                    }
                }

                oldOpportunity = getOldOpportunity();

                if (membershipProduct != null && newOpportunity != null)
                    opportunityLineItem = createOpportunityLineItem(priceBookEntry, newOpportunity);

                updateOpportunityType(newOpportunity, priceBookEntry, contact, oldOpportunity);

                if(campaignMembersList != null && campaignMembersList.size() > 0){
                    updateCampaignMembers(campaignMembersList, newOpportunity);
                }
            }
            else{
                if(contact != null){
                    renewCampaignMembersList = getCampaignMembers(contact.Id, campaignMemberIdSet);

                    if(lifetimeOpportunity != null){
                        updateOpportunityStatus(lifetimeOpportunity);
                    }
                    if(renewCampaignMembersList != null && renewCampaignMembersList.size() > 0){
                        updateCampaignMembers(renewCampaignMembersList, lifetimeOpportunity);
                    }
                    
                    PageReference demographicsInfoPage = Page.VolunteerRenewal_DemographicsInfoPage;
                    if(contact != null && contact.Id != null)
                    demographicsInfoPage.getParameters().put('ContactId', contact.Id);

                    if(councilAccount != null && councilAccount.Id != null)
                        demographicsInfoPage.getParameters().put('CouncilId', councilAccount.Id);
    
                    if(campaignMemberIds != '')
                        demographicsInfoPage.getParameters().put('CampaignMemberIds',campaignMemberIds);
    
                    if(newOpportunity != null && newOpportunity.Id != null) 
                        demographicsInfoPage.getParameters().put('OpportunityId',newOpportunity.Id);                    
            
                    demographicsInfoPage.setRedirect(true);
                    system.debug('demographicsInfoPage ===> '+demographicsInfoPage);
                    return demographicsInfoPage;
                }
            }

            if (Apexpages.currentPage().getParameters().containsKey('CampaignMemberIds')) {
                campaignMemberIds = Apexpages.currentPage().getParameters().get('CampaignMemberIds');
            }

            //Update council account
            if (booleanGrantRequested == true && councilAccount != null)
                updateCouncilAccount(councilAccount, booleanGrantRequested);

            //GSA-390 : Create a OpportunityContactRole
            if(newOpportunity != null && contact != null && newOpportunity.Id != null && contact.Id != null){
                OpportunityContactRole opportunityContactRole = createOpportunityContactRole(contact, newOpportunity);
            }

            if (booleanOppMembershipOnPaper || booleanGrantRequested) {
                PageReference landingPage = Page.VolunteerRenewal_MemberInfoThankYou;
                if(contact != null && contact.Id != null)
                    landingPage.getParameters().put('ContactId', contact.Id);

                if(councilAccount != null && councilAccount.Id != null)
                    landingPage.getParameters().put('CouncilId', councilAccount.Id);

                if(campaignMemberIds != '')
                    landingPage.getParameters().put('CampaignMemberIds',campaignMemberIds);

                if(booleanGrantRequested != null )
                    landingPage.getParameters().put('FinancialAidRequired',String.valueOf(booleanGrantRequested));

                if(booleanOppMembershipOnPaper != null )
                    landingPage.getParameters().put('CashOrCheck',String.valueOf(booleanOppMembershipOnPaper));

                if(newOpportunity != null && newOpportunity.Id != null) {
                    landingPage.getParameters().put('OpportunityId',newOpportunity.Id);
                }else if(isLifetime != true){
                    return addErrorMessageAndRollback(savepoint,'Membership Opportunity not created.');
                }


                if(oldOpportunity != null && oldOpportunity.Id != null)
                    landingPage.getParameters().put('OldOpportunityId',oldOpportunity.Id);

                if(lifetimeOpportunity != null && lifetimeOpportunity.Id != null)
                    landingPage.getParameters().put('lifetimeOpportunityId',lifetimeOpportunity.Id);
                landingPage.setRedirect(true);
                return landingPage;

            }
            if(contact != null && contact.Id != null && councilAccount.Id != null) {
                string paymenturl = '/VolunteerRenewal_Payment' + '?ContactId='+contact.Id + '&CouncilId='+councilAccount.Id+ '&CampaignMemberIds='+campaignMemberIds+'&OpportunityId='+newOpportunity.Id;
                if(oldOpportunity != null)
                paymenturl = '/VolunteerRenewal_Payment' + '?ContactId='+contact.Id  + '&CouncilId='+councilAccount.Id + '&CampaignMemberIds='+campaignMemberIds+'&OpportunityId='+newOpportunity.Id+'&OldOpportunityId='+oldOpportunity.Id;
                string paymenturlFinal = membershipYear + Label.community_login_URL + paymenturl;
                system.debug('urlDetails...' +paymenturlFinal + '***CM '+oldCampaign);
                CampaignMember oldCampaignMember = [Select Id,Pending_Payment_URL__c from CampaignMember where ID = :oldCampaign]; 
                oldCampaignMember.Pending_Payment_URL__c = paymenturlFinal;
                update oldCampaignMember;
            }
            PageReference paymentProcessingPage = Page.VolunteerRenewal_Payment;
            if(contact != null && contact.Id != null)
                paymentProcessingPage.getParameters().put('ContactId', contact.Id);

            if(councilAccount != null && councilAccount.Id != null)
                paymentProcessingPage.getParameters().put('CouncilId', councilAccount.Id);

            if(campaignMemberIds != '')
                paymentProcessingPage.getParameters().put('CampaignMemberIds',campaignMemberIds);

            if(newOpportunity != null && newOpportunity.Id != null && isLifetime == false) {
                paymentProcessingPage.getParameters().put('OpportunityId',newOpportunity.Id);
            }else if(newOpportunity == null && isLifetime == false){
                return addErrorMessageAndRollback(savepoint,'Membership Opportunity not created.');
            }
            if(lifetimeOpportunity != null && lifetimeOpportunity.Id != null && isLifetime == true)
                paymentProcessingPage.getParameters().put('OpportunityId', null);//lifetimeOpportunity.Id
            if(oldOpportunity != null && oldOpportunity.Id != null)
                paymentProcessingPage.getParameters().put('OldOpportunityId',oldOpportunity.Id);
            paymentProcessingPage.setRedirect(true);
            return paymentProcessingPage;

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

    public Contact updateContact(Contact contact) {
        Contact contactToUpdate;
        if (contact != null)
            contactToUpdate = contact;
        if (contactToUpdate != null) {
            String[] strBirthDate = dateOfBirth.trim().split('/');
            contactToUpdate.rC_Bios__Birth_Day__c=strBirthDate[1].trim();
            contactToUpdate.rC_Bios__Birth_Month__c=strBirthDate[0].trim();
            contactToUpdate.rC_Bios__Birth_Year__c=strBirthDate[2].trim();
            contactToUpdate.FirstName = firstName;
            contactToUpdate.LastName = lastName;

            if(gender != null && gender != '')
                contactToUpdate.rC_Bios__Gender__c = gender.toUpperCase().contains('NONE') ? '' : gender;

            contactToUpdate.rC_Bios__Home_Email__c = email != null ? email : '';
            contactToUpdate.rC_Bios__Work_Email__c = (email2 != null && email2 != '') ? email2 : email;

            if(preferredEmail.equalsIgnoreCase('Email'))
                contactToUpdate.rC_Bios__Preferred_Email__c = 'Home';

            if(preferredEmail.equalsIgnoreCase('Email2'))
                contactToUpdate.rC_Bios__Preferred_Email__c = 'Work';

            contactToUpdate.HomePhone = homePhone;
            contactToUpdate.rC_Bios__Work_Phone__c = businessPhone;
            contactToUpdate.MobilePhone = mobilePhone;

            /*if(preferredPhone != null && preferredPhone != '')
            contactToUpdate.rC_Bios__Preferred_Phone__c = preferredPhone.toUpperCase().contains('NONE')
                                                          ? ''
                                                          : ((preferredPhone.equalsIgnoreCase('Business Phone')) ? 'Work' : preferredPhone);*/

            if(preferredPhone.toUpperCase().contains('NONE'))
                contactToUpdate.rC_Bios__Preferred_Phone__c = '';
            else if(preferredPhone.equalsIgnoreCase('Business Phone'))
                contactToUpdate.rC_Bios__Preferred_Phone__c = 'Work';
            else if(preferredPhone.equalsIgnoreCase('Home Phone'))
                contactToUpdate.rC_Bios__Preferred_Phone__c = 'Home';
                
            contactToUpdate.Email_Opt_In__c = booleanContactEmailOptIn;
            contactToUpdate.Text_Phone_Opt_In__c = booleanContactTextPhoneOptIn;
            contactToUpdate.Photo_Opt_In__c = booleanContactPhotoOptIn;
            contactToUpdate.Volunteer_Terms_and_Conditions__c = booleanTermsAndConditions;
            system.debug('*************** before updating contact');
            update contactToUpdate;
            system.debug('== contactToUpdate :==>' + contactToUpdate);

            return contactToUpdate;
        }
        return null;
    }
/*
    public rC_Bios__Contact_Address__c populateContactAddress(Contact contact, String addrType, String addrStreetLine1, String addrStreetLine2, String addrCity,
                                     String addrState, String addrZipCode, String addrCountry, String addrCounty) {

           rC_Bios__Contact_Address__c contactAddress = null;
           List<rC_Bios__Contact_Address__c> contactAddressNewUpsertList = new List<rC_Bios__Contact_Address__c>();
           List<rC_Bios__Contact_Address__c> contactAddressOldUpdateList = new List<rC_Bios__Contact_Address__c>();

           //Step 1 calculate address hash
           rC_Bios__Address__c address = new rC_Bios__Address__c();
           address.rC_Bios__Street_Line_1__c = (addrStreetLine1 != null && addrStreetLine1 != '') ? addrStreetLine1 : null;
           address.rC_Bios__Street_Line_2__c = (addrStreetLine2 != null && addrStreetLine2 != '') ? addrStreetLine2 : null;
           address.rC_Bios__City__c = (addrCity != null && addrCity != '')? addrCity : null;
           address.rC_Bios__State__c = (addrState != null && addrState != '') ? addrState : null;
           address.rC_Bios__Postal_Code__c = addrZipCode != null && addrZipCode != '' ? addrZipCode.substring(0, 5) : null;
           address.rC_Bios__Country__c = (addrCountry != null && addrCountry != '') ? addrCountry : null;
           String addressNewUniqueKey = generateUniqueMD5(address);
           addressNewUniqueKey = addressNewUniqueKey==null ? '' : addressNewUniqueKey;
           //Step 2 get all contact address records
             //Loop
           for(rC_Bios__Contact_Address__c contactAddressRecord : [
                Select Id
                     , rC_Bios__Preferred_Mailing__c
                     , rC_Bios__Original_Street_Line_1__c
                     , rC_Bios__Original_Street_Line_2__c
                     , rC_Bios__Original_City__c
                     , rC_Bios__Original_State__c
                     , rC_Bios__Original_Postal_Code__c
                     , rC_Bios__Original_Country__c
                     , rC_Bios__Address__r.rC_Bios__County__c
                     , rC_Bios__Address__r.rC_Bios__Unique_MD5__c
                     , Contact_Address_UniqueKey__c
                From rC_Bios__Contact_Address__c
                Where rC_Bios__Contact__c =: contact.Id
            ]){
                system.debug('addressNewUniqueKey='+addressNewUniqueKey);
                system.debug('contactAddress.a.rC_Bios__Unique_MD5__c='+contactAddressRecord.rC_Bios__Address__r.rC_Bios__Unique_MD5__c);

                 //2.1 if found a contact address record with hash match and preferred set to true, set to contactAddress(nothing to do)
                 if(   addressNewUniqueKey.equals(contactAddressRecord.rC_Bios__Address__r.rC_Bios__Unique_MD5__c)
                    && contactAddressRecord.rC_Bios__Preferred_Mailing__c == true){
                       //set reference to contactAddress
                       contactAddress = contactAddressRecord;
                 }
                 //2.2 if found a contact address record with hash match and preferred not set to true, add to contactAddressNewUpsert
                 else if(   addressNewUniqueKey.equals(contactAddressRecord.rC_Bios__Address__r.rC_Bios__Unique_MD5__c)
                         && contactAddressRecord.rC_Bios__Preferred_Mailing__c != true){
                       //set reference to contactAddress
                       contactAddress = contactAddressRecord;
                       contactAddress.rC_Bios__Preferred_Mailing__c=true;
                       contactAddressNewUpsertList.add(contactAddress);
                 }
                 //2.3 if found a contact address with hash not match and preferred set to true, set preferred to false add to contactAddressOldUpdate
                 else if(   !addressNewUniqueKey.equals(contactAddressRecord.rC_Bios__Address__r.rC_Bios__Unique_MD5__c)
                         && contactAddressRecord.rC_Bios__Preferred_Mailing__c == true){
                       contactAddressRecord.rC_Bios__Preferred_Mailing__c=false;
                       contactAddressOldUpdateList.add(contactAddressRecord);
                 }
                 else{
                    //hash does not match and not preferred, ignore such records
                    continue;
                 }
            }
             //Step 3. if contactAddressFound not found
            if(null==contactAddress){
                //create new
                contactAddress = new rC_Bios__Contact_Address__c();
                contactAddress.rC_Bios__Contact__c = contact.Id;
                contactAddress.rC_Bios__Type__c = addrType != null ? addrType : '';
                contactAddress.rC_Bios__Original_Street_Line_1__c = (addrStreetLine1 != null && addrStreetLine1 != '') ? addrStreetLine1 : null;
                contactAddress.rC_Bios__Original_Street_Line_2__c = (addrStreetLine2 != null && addrStreetLine2 != '') ? addrStreetLine2 : null;
                contactAddress.rC_Bios__Original_City__c = (addrCity != null && addrCity != '') ? addrCity : null;
                contactAddress.rC_Bios__Original_State__c = (addrState != null && addrState != '') ? addrState : null;
                contactAddress.rC_Bios__Original_Postal_Code__c = addrZipCode != null ? addrZipCode : '';
                contactAddress.rC_Bios__Original_Country__c = addrCountry != null ? addrCountry : '';
                contactAddress.rC_Bios__Preferred_Mailing__c = true;
                //add to contactAddressNewUpsert
                contactAddressNewUpsertList.add(contactAddress);
            }

            Savepoint savepoint = Database.setSavepoint();

            try{
                //Step 4 if contactAddressOldUpdate has elements update
                if(!contactAddressOldUpdateList.isEmpty()){
                    update contactAddressOldUpdateList;
                }

                //Step 5 if contactAddressNewUpsert has elements upsert
                if(!contactAddressNewUpsertList.isEmpty()){
                    upsert contactAddressNewUpsertList;

                    contactAddress = contactAddressNewUpsertList.get(0);
                }

                //get saved record
                contactAddress = [
                   select Id
                        , rC_Bios__Preferred_Mailing__c
                        , rC_Bios__Original_Street_Line_1__c
                        , rC_Bios__Original_Street_Line_2__c
                        , rC_Bios__Original_City__c
                        , rC_Bios__Original_State__c
                        , rC_Bios__Original_Postal_Code__c
                        , rC_Bios__Original_Country__c
                        , rC_Bios__Address__c
                        , rC_Bios__Address__r.rC_Bios__County__c
                        , rC_Bios__Address__r.rC_Bios__Unique_MD5__c
                        , Contact_Address_UniqueKey__c
                     from rC_Bios__Contact_Address__c
                    where Id =:contactAddress.Id
                    limit 1
                ];

                //6.1 if county does not match with what user entered set
                if(!county.equals(contactAddress.rC_Bios__Address__r.rC_Bios__County__c)){
                    rC_Bios__Address__c addressRecord = new rC_Bios__Address__c(Id=contactAddress.rC_Bios__Address__c);
                    county = county==null?'':county;
                    addressRecord.rC_Bios__County__c = county;
                    //6.2 update.
                    update addressRecord;
                }

            }
            catch(Exception exceptionObj){
                system.debug('Exception in populateContactAddress :'+exceptionObj.getMessage());
                addErrorMessageAndRollback(savepoint, 'Error saving contact address.');
                return null;
            }

            return contactAddress;
    }*/

    //replaced populateContactAddress , to resolve GSA-1761
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

    public static rC_Bios__Contact_Address__c alreadyHasSameAddressPreferred(Contact contact,String addressUniqueKey){
        //check if already a contact address exist which has same uniquekey and is preferred
            List<rC_Bios__Contact_Address__c> contactAddressList = [
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
                      and rC_Bios__Address__r.rC_Bios__Unique_MD5__c = :addressUniqueKey
                ];

          if(contactAddressList==null || contactAddressList.isEmpty())
              return null;

          return contactAddressList.get(0);
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

                if(contactAddress.rC_Bios__Address__c != null)
                    addressIdSet.add(contactAddress.rC_Bios__Address__c);

                if(contactAddress.rC_Bios__Contact__r.MailingPostalCode != null && contactAddress.rC_Bios__Contact__r.MailingPostalCode != '') {
                    if(contactAddress.rC_Bios__Contact__r.MailingPostalCode.length() >= 5)
                        zipCodeSet.add(contactAddress.rC_Bios__Contact__r.MailingPostalCode.substring(0, 5));
                    else
                        zipCodeSet.add(contactAddress.rC_Bios__Contact__r.MailingPostalCode);
                }

                mailingPostalCodeToContactAddressMap.put(contactAddress.rC_Bios__Contact__r.MailingPostalCode.substring(0, 5), contactAddress);

            }
        }
        system.debug('== addressIdSet :===> ' + addressIdSet);
        system.debug('== zipCodeSet :===> ' + zipCodeSet);

        map<Id, rC_Bios__Address__c> addressMap = new map<Id, rC_Bios__Address__c>([Select Id, OwnerId From rC_Bios__Address__c where Id IN : addressIdSet]);

        Zip_Code__c[] zipCodeList = [
            Select Id
                 , Name
                 , Council__c
                 , Zip_Code_Unique__c
                 , City__c
                 , Recruiter__c
                 , Recruiter__r.IsActive
              From Zip_Code__c
             where Zip_Code_Unique__c IN :zipCodeSet
        ];

        if(zipCodeList.size() > 0) {
            for(Zip_Code__c zipCode : zipCodeList) {
                mailingPostalCodeToZipCodeMap.put(zipCode.Zip_Code_Unique__c, zipCode);
            }
        }
        system.debug('== mailingPostalCodeToZipCodeMap :===> ' + mailingPostalCodeToZipCodeMap);
        if(ContactAddressList.size() > 0) {
            Integer iCount = 0;
            for(rC_Bios__Contact_Address__c contactAddress : ContactAddressList) {
                rC_Bios__Address__c addressToUpdate = new rC_Bios__Address__c();
                iCount++;
                if(mailingPostalCodeToContactAddressMap.containsKey(contactAddress.rC_Bios__Contact__r.MailingPostalCode)) {

                    if(mailingPostalCodeToZipCodeMap.containsKey(contactAddress.rC_Bios__Contact__r.MailingPostalCode)) {
                        Zip_Code__c matchedZipCode = mailingPostalCodeToZipCodeMap.get(contactAddress.rC_Bios__Contact__r.MailingPostalCode);
                        if(matchedZipCode.Recruiter__c != null && matchedZipCode.Recruiter__r.IsActive && contactAddress.rC_Bios__Address__c != null) {
                            addressToUpdate = addressMap.get(contactAddress.rC_Bios__Address__c);
                            addressToUpdate.OwnerId = matchedZipCode.Recruiter__c;
                        } else if(defaultSystemAdminUser != null && defaultSystemAdminUser.Id != null)
                            addressToUpdate.OwnerId = defaultSystemAdminUser.Id;

                    }
                }
                addressToUpdate.rC_Bios__County__c = county;
                if(addressToUpdate != null && addressToUpdate.Id != null)
                    addressToUpdateSet.add(addressToUpdate);
                system.debug('== addressToUpdate ' + iCount + ' :===> ' + addressToUpdate);
            }
            system.debug('== addressToUpdateSet :===> ' + addressToUpdateSet);
        }
        if(addressToUpdateSet != null && addressToUpdateSet.size() > 0) {
            try {
                addressToUpdateList.clear();
                addressToUpdateList.addAll(addressToUpdateSet);

                system.debug('== addressToUpdateSet :===> ' + addressToUpdateSet);

                VolunteerRenewalUtility.addressUpdateList(addressToUpdateList);
                //update addressToUpdateList;

            } catch(Exception Ex) {
                system.debug('Exception :===>  ' + Ex.getMessage());
            }
        }
    }

    public Opportunity createMembershipOpportunity(String recordTypeId, Contact contact, Opportunity oldMembershipOpportunity, PricebookEntry priceBookEntry) {

        String campaignName = '';
        membershipYear = string.valueOf(system.today().year());
        system.debug('****** priceBookEntry'+priceBookEntry);
        if(priceBookEntry != null && priceBookEntry.Product2.Name.toUpperCase().contains('LIFETIME')) {
            campaignName = LIFETIME_MEMBERSHIP;
        }
        else if(priceBookEntry != null && priceBookEntry.Product2.rC_Giving__End_Date__c != null) {
            membershipYear = string.valueOf(priceBookEntry.Product2.rC_Giving__End_Date__c.year());
            campaignName = membershipYear + ' Membership';
        }

        Schema.DescribeSObjectResult accountDescribe = Campaign.sObjectType.getDescribe();
        System.debug('accessible:' + accountDescribe.accessible);

        system.debug('=== contact : ' + contact +'&&&& campaignName'+campaignName);
        campaignName = campaignName!=null && !String.isEmpty(campaignName)?campaignName.trim():'';
        Account[] accountList = [
            Select Id
              from Account
             where Id = :contact.AccountId
             limit 1
        ];
        system.debug('=== accountList : ' + accountList.size());

        system.debug('=== campaignName : ' + campaignName);
        Campaign[] membershipCampaignList = [
            Select Id
                 , Name
              From Campaign
             where Name = :campaignName
             limit 1
        ];
        system.debug('***********'+membershipCampaignList);
        Opportunity opportunity;
        system.debug('=== accountList : ' + accountList.size()+'membershipCampaignList==: '+membershipCampaignList);
        if (accountList != null && accountList.size() > 0 && membershipCampaignList != null && membershipCampaignList.size() >0) {
            system.debug('=== inside if : ' + accountList[0]);
            opportunity = new Opportunity(
                RecordTypeId = recordTypeId,
                AccountId = accountList[0].Id,
                CampaignId = membershipCampaignList[0].Id,
                rC_Giving__Giving_Amount__c = (priceBookEntry.UnitPrice != null) ? priceBookEntry.UnitPrice : 0,
                StageName = 'Open',
                CloseDate = System.today(),
                Membership_on_Paper__c = (booleanOppMembershipOnPaper == true) ? true : false, // Added by chandra
                Grant_Requested__c = (booleanGrantRequested == true) ? true : false,    // Added by chandra
                rC_Giving__Is_Giving__c = true,
                Membership_Year__c = membershipYear
                //Background_Check__c = oldMembershipOpportunity.Background_Check__c

            );
            system.debug('**Membership_on_Paper__c==>**: ' + opportunity.Membership_on_Paper__c);
            system.debug('== matchingZipCode Opp ==: ' + matchingZipCode);
            if(matchingZipCode.Recruiter__c != null && matchingZipCode.Recruiter__r.IsActive)
                opportunity.OwnerId = matchingZipCode.Recruiter__c;
            else if(defaultSystemAdminUser != null && defaultSystemAdminUser.Id != null)
                opportunity.OwnerId = defaultSystemAdminUser.Id;

           insert opportunity;
           // Set permission to current user
           OpportunityShare os = new OpportunityShare(OpportunityId = opportunity.id);
           os.OpportunityId = opportunity.id; // *** ERROR - not writable ***
           os.OpportunityAccessLevel = 'Read';
           os.UserOrGroupId = UserInfo.getUserId();
           insert os;
        }
       /** Code moved from here  **/
        return opportunity;
    }

    public OpportunityLineItem createOpportunityLineItem(PricebookEntry priceBookEntry, Opportunity newOpportunity) {

        OpportunityLineItem opportunityLineItem = new OpportunityLineItem();
        opportunityLineItem.PricebookEntryId = priceBookEntry.Id;
        opportunityLineItem.OpportunityId = newOpportunity.Id;
        opportunityLineItem.Quantity = 1;

        if (priceBookEntry != null)
            opportunityLineItem.UnitPrice = priceBookEntry.UnitPrice;
        //opportunityLineItem = VolunteerRenewalUtility.insertOpportunityLineItem(opportunityLineItem);
       opportunityLineItem = VolunteerRenewalUtility.insertOpportunityLineItem(opportunityLineItem);

       /*============= Code Moved for transaction opportunity Start====================*/

        List<Opportunity> transactionOppList;
        if(newOpportunity != null && newOpportunity.Id!= null) {
            transactionOppList = [
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
        }
        if(transactionOppList != null && transactionOppList.size() > 0) {
            system.debug('*********** matchingZipCode.Recruiter__c+'+matchingZipCode.Recruiter__c+' / matchingZipCode.Recruiter__r.IsActive'+matchingZipCode.Recruiter__r.IsActive);
            for(Opportunity transactionOpp : transactionOppList) {
                if(matchingZipCode.Recruiter__c != null && matchingZipCode.Recruiter__r.IsActive)
                    transactionOpp.OwnerId = matchingZipCode.Recruiter__c;
                else if(defaultSystemAdminUser != null && defaultSystemAdminUser.Id != null)
                    transactionOpp.OwnerId = defaultSystemAdminUser.Id;
            }
            system.debug('== Vol renewal transactionOppList : ' + transactionOppList);
            VolunteerRenewalUtility.updateOpportunityList(transactionOppList);
        }
        /*============= Code Moved for transaction opportunity End====================*/
        return opportunityLineItem;
    }

    public void updateOpportunityType(Opportunity newOpportunity, PricebookEntry priceBookEntry, Contact contact, Opportunity oldMembershipOpportunity) {

        if (newOpportunity != null && priceBookEntry != null) {

            newOpportunity.Type = ( priceBookEntry.Name.toUpperCase().contains('ADULT'))
                                    ? 'Adult Membership'
                                    : (     priceBookEntry.Name.toUpperCase().contains('LIFETIME')
                                            ? 'Lifetime Membership'
                                            : newOpportunity.Type );

            if(contact != null && contact.Id != null) {
                newOpportunity.Contact__c = contact.Id;
                newOpportunity.Membership_Status__c = (contact.Ineligible__c == true) ? 'Ineligible' : 'Payment Pending';
                newOpportunity.Renewal__c = true;
                newOpportunity.rC_Giving__Primary_Contact__c = contact.Id;
                newOpportunity.Adult_Email__c = (preferredEmail != null && preferredEmail != ''
                                            && preferredEmail.equalsIgnoreCase('Email2') && email2 <> '')
                                            ? email2 : email ;
            }

            if(oldMembershipOpportunity != null && oldMembershipOpportunity.Id != null && oldMembershipOpportunity.Background_Check__c != null)
                newOpportunity.Background_Check__c = oldMembershipOpportunity.Background_Check__c;

            newOpportunity = VolunteerRenewalUtility.updateOpportunity(newOpportunity);
        }
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

    public CampaignMember[] getRenewedCampaignMembers(String contactId, Set<String> renewedcampaignMemberIdSet) {
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
               and Display_Renewal__c = true
               and Active__c = true
               and Id IN :renewedcampaignMemberIdSet
               FOR UPDATE
        ];

        return (campaignMemberList != null && campaignMemberList.size() > 0) ? campaignMemberList : new List<CampaignMember>();
    }

    public void updateCampaignMembers(CampaignMember[] campaignMemberList, Opportunity opportunity) {

        List<CampaignMember> renewedCampaignMemberList = new List<CampaignMember>();
        renewedCampaignMemberList = getRenewedCampaignMembers(contact.Id, campaignMemberIdSet);
        List<CampaignMember> updateCampaignMemberList = new List<CampaignMember>();

        if (renewedCampaignMemberList != null && renewedCampaignMemberList.size() > 0 ) {
            for(CampaignMember campaignMember : renewedCampaignMemberList){
                if(isLifetime == true) {
                    campaignMember.Display_Renewal__c = false;
                }    
                campaignMember.Membership__c = opportunity.Id;
                updateCampaignMemberList.add(campaignMember);
            }
        }

        if (campaignMemberList != null && campaignMemberList.size() > 0 && opportunity != null) {

            for(CampaignMember campaignMember : campaignMemberList){
                if(campaignMember.Display_Renewal__c == null || campaignMember.Display_Renewal__c == false){
                    campaignMember.Membership__c = opportunity.Id;
                    if(isLifetime == true) {
                    campaignMember.Active__c = true;
                    }
                    updateCampaignMemberList.add(campaignMember);
                }
            }
        }
        VolunteerRenewalUtility.updateCampaignMemberList(updateCampaignMemberList);
    }

    public void updateOpportunityStatus(Opportunity lifetimeOpportunity){
        String lifetimeProductId;
        for(PricebookEntry pricebookEntry : PricebookEntryList) {
            if (pricebookEntry.Name.toUpperCase().contains('LIFETIME')){
                if(pricebookEntry.Product2Id != null)
                    lifetimeProductId = pricebookEntry.Product2Id;
            }
        }

        if(lifetimeProductId != null){
            Product2 product = [
                Select Id
                     , rC_Giving__Start_Date__c
                     , rC_Giving__End_Date__c
                 From Product2
                Where Id =: lifetimeProductId
            ];

            if(product != null && product.rC_Giving__Start_Date__c != null ){
                if(lifetimeOpportunity.Background_Check_Completion_Date__c  < product.rC_Giving__Start_Date__c){
                    lifetimeOpportunity.Membership_Status__c = 'Background Check';
                    lifetimeOpportunity = VolunteerRenewalUtility.updateOpportunity(lifetimeOpportunity);
                }
            }
        }
    }

    public void updateCouncilAccount(Account councilAccount, Boolean booleanGrantRequested) {
        try{
            councilAccount.Volunteer_Financial_Aid_Available__c = booleanGrantRequested;

            VolunteerRenewalUtility.updateCouncilAccount(councilAccount);
        } catch(Exception Ex) {
        }
    }

    public OpportunityContactRole createOpportunityContactRole(Contact contact, Opportunity newOpportunity) {

        OpportunityContactRole opportunityContactRole = new OpportunityContactRole(
            Role = 'Adult',
            OpportunityId = newOpportunity.Id,
            ContactId =  contact.Id,
            IsPrimary = true
        );

        OpportunityContactRole opportunityContactRoleNew = VolunteerRenewalUtility.opportunityContactRole(opportunityContactRole);
        return opportunityContactRoleNew;
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

        for(StateNames__c stateName : stateNamesList)
            stateNamesSortedList.add(stateName.Name);

        stateNamesSortedList.sort();
        stateOptions.add(new SelectOption('--None--', '--None--'));

        for(String stateName : stateNamesSortedList)
            stateOptions.add(new SelectOption(stateName, stateName));

        return stateOptions;
    }
}