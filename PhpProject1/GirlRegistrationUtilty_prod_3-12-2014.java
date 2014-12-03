public without sharing class GirlRegistrationUtilty {
    
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
    public static final String SCHOOL_RECORDTYPE = 'School';
    public static final String COUNCIL_RECORDTYPE = 'Council';
    public static final String VOLUNTEER_JOBS_RECORDTYPE = 'Volunteer Jobs';
    public static final String VOLUNTEER_PROJECT_RECORDTYPE = 'Volunteer Project';
    
    public static final String OPPORTUNITY_DONATION_RECORDTYPE = 'Donation';
    public static final String MEMBERSHIP_RECORDTYPE = 'Membership';

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
    
    public static Account getCouncilAccount(Id accountId) {
        
        String CouncilRecTypeId = getAccountRecordTypeId(COUNCIL_RECORDTYPE) != null ?
                                   getAccountRecordTypeId(COUNCIL_RECORDTYPE).substring(0, 15) :
                                   '';
        
        Account[] accountList = [
            Select Id
                 , Name
                 , Volunteer_Financial_Aid_Available__c
                 , RecordTypeId
                 , ParentId 
                 , Instructions__c
                 , Council_Header__c 
                 , Council_Header_Url__c
                 , Terms_Conditions__c
                 , Service_Fee__c
                 , Girl_Financial_Aid_Available__c 
                 , Payment_Campaign__c
              From Account 
             where Id = :accountId 
               and RecordTypeId = :CouncilRecTypeId limit 1
        ];
        
        return (accountList != null && accountList.size() > 0) ? accountList[0] : new Account();
    }

     public static List<Campaign> getAllVolunteerProjectCampaign(){

        List<Campaign> allCampaignList = [
            Select ParentId
                 , Name
                 , Id
                 , Grade__c
                 , Account__c
                 , Display_on_Website__c
                 , Zip_Code__c
                 , Meeting_Location__c
                 , Meeting_Day_s__c
                 , Meeting_Frequency__c
                 , rC_Volunteers__Required_Volunteer_Count__c
              From Campaign
             where Display_on_Website__c = true
               and Zip_Code__c != null
               and RecordTypeId = :VolunteerRegistrationUtilty.getCampaignRecordTypeId(VolunteerRegistrationUtilty.VOLUNTEER_PROJECT_RECORDTYPE)
        ];
        system.debug('allCampaignList=>'+allCampaignList);

        return allCampaignList;
    }

    public static Zip_Code__c getZipCode(String zipCode) {
        List<Zip_Code__c> zipCodeList;
        if(zipCode.length() > 5){
            zipCodeList =[
            Select geo_location__Longitude__s
                 , geo_location__Latitude__s
                 , Zip_Code_Unique__c
                 , Name
              From Zip_Code__c 
             where Zip_Code_Unique__c = :zipCode.substring(0, 5)
            ];
        }
        else{
            zipCodeList =[
            Select geo_location__Longitude__s
                 , geo_location__Latitude__s
                 , Zip_Code_Unique__c
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
        zipCodeList = [
            Select geo_location__Longitude__s
                 , geo_location__Latitude__s
                 , Zip_Code_Unique__c
                 , Name 
              From Zip_Code__c 
             where Zip_Code_Unique__c = :zipCodeSet
        ];
        system.debug('#########VzipCodeList#####'+zipCodeList);
        return zipCodeList;
    }

    public static void updateSiteURLAndContactForGirl(String emailUrlToUser, Contact contact){
        try{
            String customSiteUrl = '';
            Map<String, PublicSiteURL__c> siteUrlMap = PublicSiteURL__c.getAll();
            system.debug('siteUrlMap #################'+siteUrlMap );

            if(!siteUrlMap.isEmpty() && siteUrlMap.ContainsKey('Girl_Registration')){
                customSiteUrl = siteUrlMap.get('Girl_Registration').Volunteer_BaseURL__c;
                
                /*PublicSiteURL__c newSiteUrl = siteUrlMap.get('Girl_Registration');
                newSiteUrl.Girl_Site_URL1__c = '';
                
                String siteUrlLength = customSiteUrl + emailUrlToUSer;
                
                if(siteUrlLength.length() > 255){
                    newSiteUrl.Site_URL__c =  siteUrlLength.subString(0,254);
                    newSiteUrl.Girl_Site_URL1__c = siteUrlLength.subString(254, siteUrlLength.length());
                }
                else{
                    newSiteUrl.Site_URL__c =  siteUrlLength;
                }

                update newSiteUrl;
                
                if(newSiteUrl.Girl_Site_URL1__c != null && newSiteUrl.Girl_Site_URL1__c != '')
                    contact.GirlFlowPageURL__c = newSiteUrl.Site_URL__c + newSiteUrl.Girl_Site_URL1__c;
                else
                    contact.GirlFlowPageURL__c = newSiteUrl.Site_URL__c;
             */
                //contact.GirlFlowPageURL__c = newSiteUrl.Site_URL__c;
                
                contact.GirlFlowPageURL__c = customSiteUrl + emailUrlToUSer;
                contact.IsGirlFlowPageDone__c = true;
                update contact;
            }
        }
        catch(DmlException d){
            system.debug('DmlException #################'+d);
        }
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
    
    public static Account upsertAccountOwner(Account account, Id accountOwnerId){

        if(account != null && accountOwnerId != null){
            account.OwnerId = accountOwnerId;
            upsert(account);
        }
        return account;
    }
    
    public static Contact upsertContactOwner(Contact contact, Id contactOwnerId){
        system.debug('##############contactOwnerId******'+contactOwnerId);
        
        if(contact != null && contactOwnerId != null){
            contact.OwnerId = contactOwnerId;
            upsert contact;
        }
        system.debug('##############******'+contact);
        return contact;
    }
    
    public static CampaignMember createCampaignMember(Campaign campaign, Contact contact, String strRecType, Integer exceptionCount) {

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
                if (strRecType.equalsIgnoreCase('true')) {
                    campaignMember = new CampaignMember(CampaignId = campaign.Id, ContactId = contact.Id, status = 'Responded');
                }else {
                    campaignMember = new CampaignMember(CampaignId = campaign.Id, ContactId = contact.Id); 
                }

                rC_Volunteers__CampaignMember_Setting__c campaignMemberSetting = rC_Volunteers__CampaignMember_Setting__c.getInstance();
                campaignMemberSetting = campaignMemberSetting != null ? campaignMemberSetting : new rC_Volunteers__CampaignMember_Setting__c();
                Boolean disableCascade = campaignMemberSetting.rC_Volunteers__Disable_CascadeContacts__c;

                campaignMemberSetting.rC_Volunteers__Disable_CascadeContacts__c = true;
                upsert campaignMemberSetting;

                try {
                    insert campaignMember;
                } catch(Exception pException) {
                    if(pException.getMessage().contains('UNABLE_TO_LOCK_ROW')){
                        if(exceptionCount < 5) {
                            Database.rollback(savepoint);
                            createCampaignMember(campaign, contact, strRecType, exceptionCount++);
                        }
                        else
                            throw new UnableToLockRowException('Record is locked by another user. Please re-submit the page once more.');
                    }
                    else
                        throw pException;
                }

                campaignMemberSetting.rC_Volunteers__Disable_CascadeContacts__c = disableCascade;
                upsert campaignMemberSetting;
            }
            else
                campaignMember = campaignMemberList[0];
        }
        return campaignMember;
    }

    public static List<CampaignMember> campaignMemberList(string contactId ,set<Id> campaignSet) {
        List<CampaignMember> existingCampaignMemberList = [
            Select  Id
                 , contactId
                 , CampaignId
              From CampaignMember 
             Where ContactId = :contactId 
               And CampaignId IN :campaignSet
               ];
        return (existingCampaignMemberList != null && existingCampaignMemberList.size() >0) ? existingCampaignMemberList : null;
        //return existingCampaignMemberList;
    }
    
    public static List<Campaign> lstCampaign(String troopOrGroupName, String isTroopOrZip){
        String baseQuery = '';
        String unsure = 'Unsure';
        String selectQuery = 'Select Parent.Name, ParentId , Parent.Grade__c, Parent.Meeting_Day_s__c, Parent.Meeting_Location__c, Parent.rC_Volunteers__Required_Volunteer_Count__c, Parent.Display_on_Website__c, Parent.Zip_Code__c, Parent.Account__c, Id, Name, Zip_Code__c, GS_Volunteers_Required__c , Council_Code__c From Campaign ';
        String whereClause = ' where ';
        String parentZipCode = 'Parent.Zip_Code__c != null';
        String troopName = '(Parent.Name = ' + '\''+troopOrGroupName+ '\''+ ' OR Name = ' +'\'' + troopOrGroupName + '\''+ ')';
        String zipCode = 'Parent.Zip_Code__c != null';
        String displayWebSite = ' and Display_on_Website__c = true ';
        String unsureName = ' and Parent.Name != ' +'\'' + unsure + '\'';

        if(isTroopOrZip.equalsIgnoreCase('TroopAndZip'))
            baseQuery = selectQuery + whereClause + parentZipCode + ' and ' + troopName + ' and Display_on_Website__c = true '+unsureName;
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
        String selectQuery = 'Select Parent.Name, ParentId , Parent.Grade__c, Parent.Meeting_Day_s__c, Parent.Meeting_Location__c, Parent.rC_Volunteers__Required_Volunteer_Count__c, Parent.Display_on_Website__c,  Parent.Zip_Code__c, Parent.Account__c, Id, Name, Zip_Code__c, GS_Volunteers_Required__c , Council_Code__c From Campaign ';
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
    
    public static Campaign searchCampaignFromName(String campaignName) {
        Campaign[] membershipCampaignList = [Select Id, Name From Campaign where Name = :campaignName limit 1];
        system.debug('membershipCampaignList=======>'+membershipCampaignList);
        
        Campaign campaign = (membershipCampaignList != null && membershipCampaignList.size() > 0) ? membershipCampaignList[0] : new Campaign();
        system.debug('campaign=======>'+campaign);
        return campaign;
    }
    
    
    public static List<Campaign> getListOfCampaign(String troopOrGroupName, String isTroopOrZip, Set<String> zipCodeSet, String strGrade){
        String baseQuery = '';
        String unsure = 'Unsure';
        String selectQuery = 'Select Grade__c, Meeting_Day_s__c, Meeting_Frequency__c, Meeting_Location__c, Volunteers_Needed_to_Start__c , Volunteers_Needed_to_Start_New__c, Display_on_Website__c,Troop_Start_Date__c,Meeting_Start_Time__c, Account__c, Girl_Openings_Remaining__c, Participation__c, Id, Name, Zip_Code__c, Council_Code__c From Campaign';
        String whereClause = ' where ';
       // String troopName = ' Name = ' +'\'' + troopOrGroupName + '\'';
        String troopName =  ' Name Like \'%'+troopOrGroupName+'%\' ';
        String zipCode = ' Zip_Code__c != null';
        String grade = ' and Grade__c != null ';
        String displayWebSite = ' and Display_on_Website__c = true ';
        String recordType = ' and RecordTypeId = ' + '\'' + GirlRegistrationUtilty.getCampaignRecordTypeId(GirlRegistrationUtilty.VOLUNTEER_PROJECT_RECORDTYPE) + '\'';
        String unsureName = ' and Name != ' +'\'' + unsure + '\'';
        String zipCodeUniqueSet = 'and Zip_Code__c IN :zipCodeSet ';
        String orderBy = ' order By Name ASC';
            
            
        if(isTroopOrZip.equalsIgnoreCase('TroopNameAndZipCode') && strGrade != null && strGrade != '' && !strGrade.toUpperCase().contains('NONE'))
            baseQuery = selectQuery + whereClause +  troopName + displayWebSite + ' and ' + zipCode + recordType + grade + unsureName + zipCodeUniqueSet + orderBy;
        else if(isTroopOrZip.equalsIgnoreCase('TroopNameAndZipCode') && strGrade != null && strGrade.toUpperCase().contains('NONE'))
            baseQuery = selectQuery + whereClause +  troopName + displayWebSite + ' and ' + zipCode + recordType + unsureName + zipCodeUniqueSet + orderBy;
        else if(isTroopOrZip.equalsIgnoreCase('TroopName'))
            baseQuery = selectQuery + whereClause + troopName + displayWebSite + recordType + unsureName;
        else if(isTroopOrZip.equalsIgnoreCase('ZipCode') && strGrade != null && strGrade != '' && !strGrade.toUpperCase().contains('NONE'))
            baseQuery = selectQuery + whereClause + zipCode + displayWebSite + recordType + grade + unsureName +  zipCodeUniqueSet + orderBy; 
        else if(isTroopOrZip.equalsIgnoreCase('ZipCode') && strGrade != null && strGrade.toUpperCase().contains('NONE'))
            baseQuery = selectQuery + whereClause + zipCode + displayWebSite + recordType + unsureName +  zipCodeUniqueSet + orderBy;
            
        system.debug('==========>'+baseQuery);
        List<Campaign> campaignMemberList = database.query(baseQuery);
        system.debug('=====campaignMemberList=====>'+campaignMemberList);
        return campaignMemberList;
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
    
    public static List<Zip_Code__c> getAllZipCodeWithingSelectedRadius(String doubleLatitude, String doubleLongitude, String radius) {

        String strQuery = 'Select geo_location__c, geo_location__Longitude__s , geo_location__Latitude__s , Zip_Code_Unique__c From Zip_Code__c WHERE DISTANCE(geo_location__c, GEOLOCATION('
                       + decimal.valueOf(doubleLatitude) + ', ' + decimal.valueOf(doubleLongitude) + '),' + '\''+ 'mi' + '\'' + ')' + ' < ' + Integer.valueOf(radius);
    
        List<Zip_Code__c> zipCodeList = Database.query(strQuery);
        system.debug('zipCodeList#################'+zipCodeList);
        system.debug('zipCodeList#################'+zipCodeList.size());
        return zipCodeList;
    }
    
}