public without sharing class VolunteerRenewalUtility {

    private static final map<String, Schema.RecordTypeInfo> CAMPAIGN_RECORDTYPE_INFO_MAP =  Campaign.SObjectType.getDescribe().getRecordTypeInfosByName();
    private static final map<String, Schema.RecordTypeInfo> ACCOUNT_RECORDTYPE_INFO_MAP =  Account.SObjectType.getDescribe().getRecordTypeInfosByName();
    private static final map<String, Schema.RecordTypeInfo> CONTACT_RECORDTYPE_INFO_MAP =  Contact.SObjectType.getDescribe().getRecordTypeInfosByName();
    private static final map<String, Schema.RecordTypeInfo> OPPORTUNITY_RECORDTYPE_INFO_MAP =  Opportunity.SObjectType.getDescribe().getRecordTypeInfosByName();

    private static final String RT_VOLUNTEER_JOBS = 'Volunteer Jobs';
    private static final String RT_VOLUNTEER_PROJECT = 'Volunteer Project';
    public static final String RT_VOLUNTEER_JOBS_ID = getCampaignRecordTypeId(RT_VOLUNTEER_JOBS);
    public static final String RT_VOLUNTEER_PROJECT_ID = getCampaignRecordTypeId(RT_VOLUNTEER_PROJECT);

    public map<String, map<String, Id>> sobjectToRecordTypeMap = new map<String, map<String, Id>>();
    public static final String HOUSEHOLD_RECORDTYPE = 'Household';
    public static final String COUNCIL_RECORDTYPE = 'Council';
    public static final String VOLUNTEER_JOBS_RECORDTYPE = 'Volunteer Jobs';
    public static final String VOLUNTEER_PROJECT_RECORDTYPE = 'Volunteer Project';

    public static final String OPPORTUNITY_DONATION_RECORDTYPE = 'Donation';

    public class UnableToLockRowException extends Exception {}

    public static String getCampaignRecordTypeId(String name) {
       return (CAMPAIGN_RECORDTYPE_INFO_MAP.get(name) != null) ? CAMPAIGN_RECORDTYPE_INFO_MAP.get(name).getRecordTypeId() : null;
    }
    public static String getAccountRecordTypeId(String name) {
       return (ACCOUNT_RECORDTYPE_INFO_MAP.get(name) != null) ? ACCOUNT_RECORDTYPE_INFO_MAP.get(name).getRecordTypeId() : null;
    }
    public static String getContactRecordTypeId(String name) {
       return (CONTACT_RECORDTYPE_INFO_MAP.get(name) != null) ? CONTACT_RECORDTYPE_INFO_MAP.get(name).getRecordTypeId() : null;
    }
    public static String getOpportunityRecordTypeId(String name) {
       return (OPPORTUNITY_RECORDTYPE_INFO_MAP.get(name) != null) ? OPPORTUNITY_RECORDTYPE_INFO_MAP.get(name).getRecordTypeId() : null;
    }
    public static Zip_Code__c getZipCode(String zipCode) {
        List<Zip_Code__c> zipCodeList;
         if(zipCode.length() > 5){
             zipCodeList=[
               Select geo_location__Longitude__s
                 , geo_location__Latitude__s
                 , Zip_Code_Unique__c
                 , Council__c
                 , Name
              From Zip_Code__c 
             where Zip_Code_Unique__c = :zipCode.substring(0, 5)
            ];
         }else{
             zipCodeList=[
               Select geo_location__Longitude__s
                 , geo_location__Latitude__s
                 , Zip_Code_Unique__c
                 , Council__c
                 , Name
              From Zip_Code__c 
             where Zip_Code_Unique__c = :zipCode
            ];
         }
         
        system.debug('zipCodeList in utility===>'+zipCodeList);
        Zip_Code__c objZipCode = (zipCodeList != null && zipCodeList.size() > 0) ? zipCodeList[0] : new Zip_Code__c();
        return objZipCode;
    }
    
    public static List<Zip_Code__c> getZipCodeList(Set<String> zipCodeSet){
        List<Zip_Code__c> zipCodeList = new List<Zip_Code__c>();
        zipCodeList = [Select geo_location__Longitude__s
                             , geo_location__Latitude__s
                             , Zip_Code_Unique__c
                             , Name 
                          From Zip_Code__c 
                         where Zip_Code_Unique__c = :zipCodeSet];
         system.debug('#########VzipCodeList#####'+zipCodeList);
        return zipCodeList;
    }

    public static User getSystemAdminUser(){
        List<User> userList = [
            Select Id
                 , LastName
                 , IsActive
                 , Profile.Name
                 , Profile.Id
                 , ProfileId 
              From User 
             where Name = :Label.Volunteer_And_Girl_Registration_System_Admin_User_Name
               and IsActive = true 
               and UserRoleId != null
             limit 1
        ];
        system.debug('userList #################'+userList);
        User user =  (userList != null && userList.size() >0) ? userList[0] : new User();
        system.debug('user #################'+user);
        return user;
    }

    public static List<Campaign> getAllVolunteerJobCampaign(){
        
        List<Campaign> allCampaignList = [Select ParentId
                                               , Name
                                               , Id
                                               , Grade__c
                                               , Account__c
                                               , Display_on_Website__c
                                               , Zip_Code__c
                                               , Meeting_Location__c
                                               , Meeting_Start_Date_time__c
                                               , Meeting_Day_s__c
                                               , rC_Volunteers__Required_Volunteer_Count__c
                                            From Campaign
                                           where Display_on_Website__c = true
                                             and Zip_Code__c != null
                                             and RecordTypeId = :RT_VOLUNTEER_JOBS_ID
        ];
        system.debug('allCampaignList=>'+allCampaignList);
        return allCampaignList;
    }
    public static CampaignMember campaignMemberAlreadyPresent(String campaign, String contact) {
         CampaignMember[] campaignMemberList = [
                Select Id
                     , ContactId
                     , CampaignId 
                  From CampaignMember 
                 where CampaignId = :campaign 
                   and ContactId = :contact
            ];  
         return (campaignMemberList != null && campaignMemberList.size() > 0) ? campaignMemberList[0] : null;
    }
    public static List<Campaign> getAllVolunteerProjectCampaign(){
        
        List<Campaign> allCampaignList = [Select ParentId
                                               , Name
                                               , Id
                                               , Grade__c
                                               , Account__c
                                               , Display_on_Website__c
                                               , Zip_Code__c
                                               , Meeting_Location__c
                                               , Meeting_Start_Date_time__c
                                               , Meeting_Day_s__c
                                               , rC_Volunteers__Required_Volunteer_Count__c
                                            From Campaign
                                           where Display_on_Website__c = true
                                             and Zip_Code__c != null
                                             and RecordTypeId = : getCampaignRecordTypeId(VOLUNTEER_PROJECT_RECORDTYPE)
        ];
        system.debug('allCampaignList=>'+allCampaignList);
       
        return allCampaignList;
    }
    
    public static List<Campaign> getChildCampaignsWithName(String troopOrGroupName){
        List<Campaign> allCampaignWithNameList = [Select ParentId
                                               , Name
                                               , Id
                                               , Grade__c
                                               , Account__c
                                               , Display_on_Website__c
                                               , Zip_Code__c
                                               , Meeting_Location__c
                                               , Meeting_Start_Date_time__c
                                               , Meeting_Day_s__c
                                               , rC_Volunteers__Required_Volunteer_Count__c
                                            From Campaign
                                           where Display_on_Website__c = true
                                             and ParentId != null
                                             and Name = :troopOrGroupName
                                             and RecordTypeId = :RT_VOLUNTEER_JOBS_ID
        ];
        system.debug('allCampaignWithNameList=>'+allCampaignWithNameList);
        return allCampaignWithNameList;
    }
    
    public static Account getCouncilAccount(Id accountId) {
        
        String CouncilRecTypeId = getAccountRecordTypeId(COUNCIL_RECORDTYPE) != null ?
                                   getAccountRecordTypeId(COUNCIL_RECORDTYPE).substring(0, 15) :
                                   null;
        
        Account[] accountList = [Select Id
                                , Name
                                , Volunteer_Financial_Aid_Available__c
                                , RecordTypeId
                                , ParentId 
                                , Instructions__c
                                , Council_Header__c
                                , Terms_Conditions__c
                                , Girl_Financial_Aid_Available__c
                                , Payment_Campaign__c
                           From Account 
                           where Id = :accountId 
                           and RecordTypeId = :CouncilRecTypeId limit 1];
        system.debug('***accountList***'+accountList);
        return (accountList != null && accountList.size() > 0) ? accountList[0] : null;
    }

    public static void updateSiteURLAndContact(String emailUrlToUser, Contact contact){
        String customSiteUrl = '';
        Map<String, PublicSiteURL__c> siteUrlMap = PublicSiteURL__c.getAll();

        if(!siteUrlMap.isEmpty() && siteUrlMap.ContainsKey('Volunteer_Registration')){
            customSiteUrl = siteUrlMap.get('Volunteer_Registration').Volunteer_BaseURL__c;

            /*PublicSiteURL__c newSiteUrl = siteUrlMap.get('Volunteer_Registration');
            String siteUrlLength = customSiteUrl + emailUrlToUSer;
            
            if(siteUrlLength.length() > 255){
                newSiteUrl.Site_URL__c =  siteUrlLength.subString(0,254);
                newSiteUrl.Site_URL1__c = siteUrlLength.subString(255, siteUrlLength.length());
            }
             else{
                newSiteUrl.Site_URL__c =  siteUrlLength;
             }
            //newSiteUrl.Site_URL__c =  customSiteUrl + emailUrlToUSer; //customSiteUrl + 'Volunteer_ThankYou' + '?ContactId='+contact.Id + '&CouncilId='+councilAccount.Id+'&CampaignMemberIds='+campaignMembers+'&OpportunityId='+newOpportunity.Id+'&FinancialAidRequired='+String.valueOf(booleanGrantRequested)+'&CashOrCheck='+String.valueOf(booleanOppMembershipOnPaper);
           
            update newSiteUrl;
            
            if(newSiteUrl.Site_URL1__c != null && newSiteUrl.Site_URL1__c != '')
                contact.VolunteerPage1URL__c = newSiteUrl.Site_URL__c + newSiteUrl.Site_URL1__c;
            else
                contact.VolunteerPage1URL__c = newSiteUrl.Site_URL__c;
                */
             
            contact.VolunteerPage1URL__c = customSiteUrl + emailUrlToUser;
            
            contact.IsVoluntter1stPageDone__c = true;
            update contact;
        }
    }

    public static Account upsertAccountOwner(Account account, Id accountOwnerId){
        
        if(account != null){
            account.OwnerId = accountOwnerId;
            Database.Upsertresult dbUpdate = database.upsert(account);
        }
        return account;
    }
    
    public static Contact upsertContactOwner(Contact contact, Id contactOwnerId){
        
        if(contact != null){
            contact.OwnerId = contactOwnerId;
            Database.Upsertresult dbUpdate = database.upsert(contact);
        }
        return contact;
    }
    
    public static CampaignMember createCampaignMember(Campaign campaign, Contact contact, Integer exceptionCount) {

        Savepoint savepoint = Database.setSavepoint();
        CampaignMember campaignMember;

        if(campaign != null && campaign.Id != null && contact != null && contact.Id != null) {
            CampaignMember[] campaignMemberList = [
                Select Id
                     , ContactId
                     , CampaignId 
                  From CampaignMember 
                 where CampaignId = :campaign.Id 
                   and ContactId = :contact.Id
            ];
            if(campaignMemberList.size() == 0) {
                campaignMember = new CampaignMember(CampaignId = campaign.Id, ContactId = contact.Id);
                try {
                    insert campaignMember;
                } catch(Exception pException) {
                    if(pException.getMessage().contains('UNABLE_TO_LOCK_ROW')){
                        if(exceptionCount < 5) {
                            Database.rollback(savepoint);
                            createCampaignMember(campaign, contact, exceptionCount++);
                        }
                        else
                            throw new UnableToLockRowException('Record is locked by another user. Please re-submit the page once more.');
                    }
                    else
                        throw pException;
                }
            }
            else
                campaignMember = campaignMemberList[0];
        }
        return campaignMember;
    }
    
    public static List<Contact> getContactList(Id userContactId) {
    
        system.debug('***userContactIdQuery***'+userContactId);
        List<Contact> contactsList = [
            Select AccountId
                 , MailingPostalCode
                 , Name
                 ,(Select Id
                     , CampaignId
                     , Membership__c
                     , Active__c
                     , Date_Active__c 
                     , Primary__c 
                     , Assignment_Type__c
                     , Renewable__c 
                     , Display_Renewal__c
                     , Campaign.Participation__c
                     , Campaign.Job_Code_Category__c
                     , Campaign.Job_Code_Sub_Category__c
                     , Pending_Payment_URL__c
                  From CampaignMembers
                 where Display_Renewal__c = true
                   and Active__c = true
                   and (Assignment_Type__c = 'Volunteer' or Assignment_Type__c = 'Adult')
                   //and Campaign.Participation__c = 'Troop'
                   //and Campaign.Job_Code_Category__c = 'Direct Service'
                   //and Campaign.Job_Code_Sub_Category__c = 'Primary'
              order By CreatedDate asc
                   )
              From Contact
             Where Id =: userContactId
        ];
        
        
        system.debug('*** userContactId  ***'+userContactId);
        system.debug('***contactsListQuery***'+contactsList);
        system.debug('***contactsListQuery***'+contactsList[0].CampaignMembers);
        return contactsList;
    }

    public static Contact getContact(String contactId) {
        Contact[] contactList = [
            Select Id
                 , AccountId
                 , Account.OwnerId
                 , FirstName
                 , LastName
                 , Birthdate
                 , HomePhone
                 , MobilePhone
                 , MailingCity
                 , MailingState
                 , MailingCountry
                 , MailingPostalCode
                 , rC_Bios__Home_Email__c
                 , rC_Bios__Work_Email__c
                 , rC_Bios__Preferred_Email__c
                 , rC_Bios__Work_Phone__c
                 , rC_Bios__Preferred_Phone__c
                 , rC_Bios__Gender__c
              From Contact 
             where Id = :contactId limit 1
        ];
        system.debug('contactList===>'+contactList);
        return (contactList != null && contactList.size() > 0) ? contactList[0] : null;
    }
    
    public static List<Campaign> getCampaignList(Set<String> campaignIdSet){
        return [Select Parent.Name, ParentId, Name, Id, Participation__c From Campaign where Id IN :campaignIdSet];
        
    }
    
    public static void updateContactAddress(List<rC_Bios__Contact_Address__c> updateContactAddressList) {
        system.debug('==utility updatetobeContactAddressList :====>  ' + updateContactAddressList);
        try {
            update updateContactAddressList;
            system.debug('==utility updateContactAddressList :====>  ' + updateContactAddressList);
        } catch(Exception Ex) {
            system.debug('== Address Exception :====>  ' + ex.getMessage());
        }
    }
    
    public static rC_Bios__Contact_Address__c insertContactAddress(rC_Bios__Contact_Address__c contactAddress) {
       system.debug('=== inside insertContactAddress === ');
       try{
            system.debug('=== inside insertContactAddress === TRY ');
            insert contactAddress;
       }catch(Exception ex){
            system.debug('Exception in Insert is : '+ ex);
       }
            
            system.debug('== contactAddress :==>' + contactAddress);
            //return contactAddress;
        return contactAddress;
    }
    
    public static List<CampaignMember> getCampaignMembers(Set<String> CampaignIdSet, Contact loggedInContact) {
        List<CampaignMember> CampaignMemberList = [
                Select Id
                     , CampaignId
                     , Active__c
                     , End_Date__c
                     , Display_Renewal__c
                     , Membership__c
                  from CampaignMember
                 Where CampaignId IN :CampaignIdSet
                   and ContactId = :loggedInContact.Id
        ];
        return (CampaignMemberList != null && CampaignMemberList.size() > 0) ? CampaignMemberList : null;
    }
    
    /*public static List<CampaignMember> getNonRenewedCampaignMember(Set<String> rolesToBeNotRenewCampaignIdSet, Contact loggedInContact) {
        List<CampaignMember> nonRenewedCampaignMemberList = [
                Select Id
                     , CampaignId
                     , Active__c
                     , End_Date__c
                     , Display_Renewal__c
                     , Membership__c
                  from CampaignMember
                 Where CampaignId IN :rolesToBeNotRenewCampaignIdSet
                   and ContactId = :loggedInContact.Id
            ];
        return (nonRenewedCampaignMemberList != null && nonRenewedCampaignMemberList.size() > 0) ? nonRenewedCampaignMemberList : null;
    }*/
    
    public static List<CampaignMember> campaignMembers(String contactName, String troopName) {
        List<CampaignMember> campaignMemberList = [
            Select Id 
                 , Active__c
                 , Display_Renewal__c
                 , Contact.Name
                 , ContactId
                 , Campaign.Name
              From CampaignMember
             Where Contact.Name = :contactName
               and Campaign.Name = :troopName
         ];
        system.debug('***campaignMemberList***'+campaignMemberList);
        return (campaignMemberList != null && campaignMemberList.size() > 0) ? campaignMemberList : null;
    }
    public static void updateCampaignMemberList(List<CampaignMember>  updateCampaignMemberList){
        if(updateCampaignMemberList <> NULL && updateCampaignMemberList.size() > 0)
            update updateCampaignMemberList;
            system.debug('***updateCampaignMemberList***'+updateCampaignMemberList);
    }
    public static void insertCampaignMemberList(List<CampaignMember> campaignMemberListToInsert){
        system.debug('$$$$$ campaignMemberListToInsert : ' + campaignMemberListToInsert);
        insert campaignMemberListToInsert;
        system.debug('***insertCampaignMemberListUtility***'+campaignMemberListToInsert);   
    }
    public static Opportunity createOpportunity(Opportunity  opportunity){
        system.debug('###########################'+Userinfo.getProfileId());
        system.debug('###########Userinfo.getUserId()################'+Userinfo.getUserId());
        
        system.debug('***Opportunity***'+opportunity);
        insert opportunity;
        system.debug('***insertopportunity***'+opportunity);
        return opportunity;
    }

    public static Opportunity updateOpportunity(Opportunity  opportunity){
        system.debug('updateopportunity ===> '+opportunity);
        update opportunity;
        system.debug('updateopportunity ===> '+opportunity);
        return opportunity != null  ? opportunity : null;
    }

    public static List<Opportunity> updateOpportunityList(List<Opportunity> opportunityList){
        update opportunityList;
        system.debug('1. opportunityList ===> '+opportunityList);
        return opportunityList;
    }

    public static List<Opportunity> insertOpportunityList(List<Opportunity>  opportunityList){
        system.debug('opportunityList ===> '+opportunityList);
        upsert opportunityList;
        system.debug('opportunityList ===> '+opportunityList);
        return (opportunityList != null && opportunityList.size() > 0) ? opportunityList : null;
    }

    public static OpportunityContactRole opportunityContactRole(OpportunityContactRole  opportunityContactRole){
        insert opportunityContactRole;
        return opportunityContactRole;
    }

    public static Contact contactRecord(String contactId) {
        List<Contact> contactList = [
            Select Id
                 , Name 
              From Contact 
             Where Id = :contactId
        ];
        return (contactList != null && contactList.size() > 0) ? contactList[0] : null;
    }

    public static Campaign campaignRecord(String campaignId) {
        List<Campaign> campaignList = [
            Select Id
                 , Name 
              From Campaign 
              Where Id = :campaignId//volunteerRenewalTroopGroupRoleSearch.parentCampaignWrapperList[0].childCampaignId
              ];
        return (campaignList != null && campaignList.size() > 0) ? campaignList[0] : null;
    }

    public static CampaignMember campaignMemberRecord(String contactId ,String campaignId) {
        List<CampaignMember> campaignMemberList =[
            Select Id
                 , ContactId
                 , CampaignId 
              From CampaignMember 
             Where ContactId = :contactId 
             And   CampaignId = :campaignId
             ];
        system.debug('***campaignMemberList***'+campaignMemberList);
        return (campaignMemberList != null && campaignMemberList.size() > 0) ? campaignMemberList[0] : null;
    }
    
    public static List<Campaign> remoteCampaignList(String strQuery) {
        List<Campaign> remoteCampaignList = database.query(strQuery);
        return (remoteCampaignList != null && remoteCampaignList.size() > 0) ? remoteCampaignList: null;
        
    } 
    public static void taskList(List<Task> taskList) {
        if(taskList <> NULL && taskList.size()>0) 
        insert taskList;
    } 
    /*public static List<CampaignMember> campaignListToInsert(List<CampaignMember> campaignMemberList) {
        if(campaignMemberList <> NULL && campaignMemberList.size()>0)
        List<Database.Saveresult> lstSaveResult = database.insert(campaignMemberList);
        return lstSaveResult;
    }*/ 
    public static void campaignMembersToInsert(List<CampaignMember> campaignMemberList) {
        if(campaignMemberList <> NULL && campaignMemberList.size()>0)
            insert campaignMemberList;
    } 
    
    public static OpportunityLineItem insertOpportunityLineItem(OpportunityLineItem opportunityLineItem) {
            insert opportunityLineItem;
            return opportunityLineItem;
    } 
    
    public static List<OpportunityLineItem> insertOpportunityLineItemList(List<OpportunityLineItem> opportunityLineItemList) {
            insert opportunityLineItemList;
            return opportunityLineItemList;
    }
    
    public static void addressUpdateList(List<rC_Bios__Address__c> addressToUpdate) {
            update addressToUpdate;
    } 
    
    public static void updateCouncilAccount(Account account) {
            update account;
    } 
    
    public static Opportunity getOpportunityGivingList(string opportunityId) {
        List<Opportunity> opportunityList = [
            Select Id
                 , Name
                 , rC_Giving__Giving_Amount__c
                 , Contact__c
                 , Auto_Giving__c
                 , Amount
              From Opportunity
             Where Id = :OpportunityId
        ];
        return (opportunityList != null && opportunityList.size() > 0) ? opportunityList[0] : null;
    }
    
    public static List<PricebookEntry > getPricebookEntryList() {
            List<PricebookEntry > PricebookEntryList = [
                Select Id
                     , Name
                     , Pricebook2Id
                     , Product2Id
                     , UnitPrice
                     , Pricebook2.Id
                     , Pricebook2.Name
                     , Pricebook2.IsActive
                     , Pricebook2.Description
                  From PricebookEntry 
                 where Pricebook2.Name = 'Girl Scouts USA'
                   and Pricebook2.IsActive = true
            ];
            return PricebookEntryList;
    } 
    
    public static List<Opportunity > getOpportunityList(string givingId) {
        List<Opportunity> opportunityList = [
            SELECT StageName
                 , Membership_Status__c
                 , Id
                 , Background_Check__r.Expiration_Date__c 
                 , Background_Check__r.Contact__c
                 , Background_Check__c
              FROM Opportunity
             WHERE Id = :givingId
        ];
        return (opportunityList != null && opportunityList.size() > 0) ? opportunityList : null;
    }
    
    public static List<Contact> contactList(string contactId) {
        List<Contact> contactOwnerList = [
            Select Name
                 , OwnerId
                 , Id 
                 , MailingPostalCode
              from Contact 
             where Id = :contactId
        ];
        return (contactOwnerList != null && contactOwnerList.size() > 0) ? contactOwnerList : null;
    }
    
      public static List<Contact> contactandcampaignList(string contactId) {
        List<Contact> contactList = [
            Select AccountId
                    , MailingPostalCode
                    , Welcome_Complete__c
                    , Secondary_Role__c
                    ,(Select Id
                           , CampaignId
                           , Campaign.Name
                           , Membership__c
                           , Active__c
                           , Date_Active__c 
                           , Primary__c 
                           , ContactId  
                        From CampaignMembers
                        Where Welcome__c = true 
                    order By CreatedDate asc)  
                 From Contact c
                Where Id =: contactId
        ];
        return (contactList != null && contactList.size() > 0) ? contactList : null;
    }
    
    public static Contact contact(string contactId) {
        Contact contactOwner = [
            Select Name
                 , LastName
                 , Id
                 , AccountId 
              from Contact 
             where Id = :contactId
             limit 1
        ];
        return (contactOwner != null) ? contactOwner : null;
    }
    
    public static List<OpportunityLineItem> getOpportunityLineItem(Opportunity opportunity) {
        List<OpportunityLineItem> opportunityLineItemList = [
            Select PricebookEntry.Product2.rC_Giving__End_Date__c
                 , PricebookEntry.Product2.rC_Giving__Start_Date__c
              From OpportunityLineItem
             Where OpportunityId =: opportunity.Id
        ];
        return (opportunityLineItemList != null && opportunityLineItemList.size() >0) ? opportunityLineItemList : null;
    }
    
    public static String getStateAbbreviation(String stateName) {
        Map<String, StateNames__c> stateNamesMap = StateNames__c.getAll();
        String stateAbbreviation = '';

        if(stateName != null && !stateNamesMap.isEmpty() && stateNamesMap.containsKey(stateName))
            stateAbbreviation = stateNamesMap.get(stateName).Abbreviation__c;

        return stateAbbreviation;
    }
    
    public static String getStateName(String stateAbbreviation) {
        Map<String, StateNames__c> stateNamesMap = StateNames__c.getAll();
        String stateName = '';
        
        if(!stateNamesMap.isEmpty()) {
            for(StateNames__c  stateNames : stateNamesMap.values()) {
                if(stateNames.Abbreviation__c != null && stateNames.Abbreviation__c.equalsIgnoreCase(stateAbbreviation)){
                    stateName = stateNames.Name;
                    break;
                }
            }
        }

        return stateName;
    }
    
    public static List<Campaign> lstCampaign(String troopOrGroupName, String isTroopOrZip){
        String baseQuery = '';
        String unsure = 'Unsure';
        String selectQuery = 'Select Parent.Name, ParentId , Parent.Grade__c, Parent.Meeting_Day_s__c, Parent.Meeting_Location__c, Parent.rC_Volunteers__Required_Volunteer_Count__c, Parent.Display_on_Website__c, Parent.Meeting_Start_Date_time__c, Parent.Zip_Code__c, Parent.Account__c, Id, Name, Zip_Code__c, GS_Volunteers_Required__c , Council_Code__c From Campaign ';
        String whereClause = ' where ';
        String parentZipCode = 'Parent.Zip_Code__c != null';
        String troopName = '(Parent.Name = ' + '\''+troopOrGroupName+ '\''+ ' OR Name = ' +'\'' + troopOrGroupName + '\''+ ')';
        String zipCode = 'Parent.Zip_Code__c != null';
        String displayWebSite = ' and Display_on_Website__c = true ';
        String unsureName = ' and Parent.Name != ' +'\'' + unsure + '\'';
        if(isTroopOrZip.equalsIgnoreCase('TroopAndZip'))
            baseQuery = selectQuery + whereClause + parentZipCode + ' and ' + troopName + ' and Display_on_Website__c = true ' +unsureName;
        else if(isTroopOrZip.equalsIgnoreCase('troopName'))
            baseQuery = selectQuery + whereClause + troopName +displayWebSite +unsureName;// ' and Display_on_Website__c = true ';
        else if(isTroopOrZip.equalsIgnoreCase('zip'))
            baseQuery = selectQuery + whereClause + zipCode + displayWebSite +unsureName;//' and Display_on_Website__c = true ';

        system.debug('==========>'+baseQuery);
        List<Campaign> campaignMemberList = database.query(baseQuery);
        system.debug('======campaignMemberList====>'+campaignMemberList);
        return campaignMemberList;
         /*          
        Strign baseQuery = '';
        String selectQuery = 'Select Parent.Name, ParentId , Parent.Grade__c, Parent.Meeting_Day_s__c, Parent.Meeting_Location__c, Parent.rC_Volunteers__Required_Volunteer_Count__c, Parent.Display_on_Website__c, Parent.Meeting_Start_Date_time__c, Parent.Zip_Code__c, Parent.Account__c, Id, Name, Zip_Code__c, GS_Volunteers_Required__c , Council_Code__c From Campaign ';
        String whereClause = ' where ';
        
        String troopNameAndZip = 'where Parent.Zip_Code__c != null and (Parent.Name = :troopOrGroupName OR Name = :troopOrGroupName) and Display_on_Website__c = true';
        String strTroopName = 'where (Parent.Name = :troopOrGroupName OR Name = :troopOrGroupName) and Display_on_Website__c = true'; 
        String strZip = 'where Parent.Zip_Code__c != null and Display_on_Website__c = true';
        
        String parentZipCode = 'Parent.Zip_Code__c != null';
        String troppName = '(Parent.Name = :troopOrGroupName OR Name = :troopOrGroupName)';
        String zipCode = 'Parent.Zip_Code__c';
        
        if(str.equalsIgnoreCase('TroopAndZip'))
            baseQuery = selectQuery + whereClause + parentZipCode + ' and ' + troppName + ' and Display_on_Website__c = true ';
        else if(str.equalsIgnoreCase('troopName'))
            baseQuery = selectQuery + whereClause + troppName + ' and Display_on_Website__c = true ';
        else if(str.equalsIgnoreCase('zip'))
            baseQuery = selectQuery + whereClause + zipCode + ' and Display_on_Website__c = true ';
            */
    }
    
}