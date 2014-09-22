<apex:page sidebar="false" showHeader="false" standardStylesheets="false" controller="Girl_TroopGroupRoleSearchController" cache="false">
    
    
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
    <script>  
     $.noConflict();
jQuery(document).ready(function(){
         //alert('test');
           // jQuery(".btnnextsure").show();
           jQuery(".btnnextsureprocessing").hide();
    
            jQuery(".btnnextsure").click(function(){
               
                jQuery(".btnnextsure").hide();
                jQuery(".btnnextsureprocessing").show();
               jQuery('#mynextbtnPopup').modal('toggle');
            });
            
            jQuery(".btnnextunsure").show();
           jQuery(".btnnextunsureprocessing").hide();
    
           jQuery(".btnnextunsure").click(function(){
                    jQuery(".btnnextunsure").hide();
                    jQuery(".btnnextunsureprocessing").show();
                
                var whyUnsure = jQuery(".whyAreYouUnsureClass").val();
                if (whyUnsure === null || whyUnsure === '') {
                    jQuery(".btnnextunsure").show();
                    jQuery(".btnnextunsureprocessing").hide();
                } 
            }); 
            });
            </script>
     <script type="text/javascript" language="javascript">
function DisableBackButton() {
window.history.forward()
}
DisableBackButton();
window.onload = DisableBackButton;
window.onpageshow = function(evt) { if (evt.persisted) DisableBackButton() }
window.onunload = function() { void (0) }
</script>
    <apex:includeScript value="https://code.jquery.com/jquery-1.9.1.min.js" />
    <apex:includeScript value="https://code.jquery.com/ui/1.10.3/jquery-ui.js" />
    
    <apex:includeScript value="{!URLFOR($Resource.timezone_lib, '/moment/moment.min.js')}" />
    <apex:includeScript value="{!URLFOR($Resource.timezone_lib, '/jstz/jstz.min.js')}" />
    <apex:includeScript value="{!URLFOR($Resource.timezone_lib, '/moment/moment-timezone-with-data.min.js')}" />

    <apex:stylesheet value="{!URLFOR($Resource.Bootstrap, '/Bootstrap/css/bootstrap.css')}" /> 
    <apex:stylesheet value="{!URLFOR($Resource.Bootstrap, '/Bootstrap/css/bootstrap.min.css')}" />  

    <apex:stylesheet value="{!URLFOR($Resource.Bootstrap, '/Bootstrap/css/bootstrap-theme.css')}" />
    <apex:stylesheet value="{!URLFOR($Resource.Bootstrap, '/Bootstrap/css/bootstrap-theme.min.css')}" />
 
    <apex:includeScript value="{!URLFOR($Resource.Bootstrap, '/Bootstrap/js/bootstrap.js')}" />
    <apex:includeScript value="{!URLFOR($Resource.Bootstrap, '/Bootstrap/js/bootstrap.min.js')}" />
    
    <!-- for DateTimePicker -->
    <!--     <apex:includeScript value="{!URLFOR($Resource.Bootstrap, '/Bootstrap/js/moment.js')}" /> -->
    <apex:includeScript value="{!URLFOR($Resource.Bootstrap, '/Bootstrap/js/bootstrap-datetimepicker.js')}" />
    <apex:stylesheet value="{!URLFOR($Resource.Bootstrap, '/Bootstrap/css/bootstrap-datetimepicker.css')}" />
    
    <apex:stylesheet value="https://code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css" />
    
   <apex:stylesheet value="{!$Resource.ScreenCss}" />
 
    
    <style>
.form-control {
    background-image: none;
    !
    important
}

.select-list {
    -webkit-appearance: none;
    border-radius: 3px;
    background-image: url("{!$Resource.DownArrow}");
    background-repeat: no-repeat;
    background-position: 98% 10px;
    -webkit-appearance: none;
    -moz-appearance: none;
    text-indent: 0.01px;
    text-overflow: "";
    appearence: none;
  
}
   .button_reg{
        font-family:Open Sans,Arial!important;
        font-size: 16px!important;
        font-weight:bold!important;
        padding: 6px 10px 6px 8px!important;
        height: 35px!important;
        color: #fff!important;
        text-decoration: none!important;
        text-align: center!important;
        background: #00AE58!important;
        width:100%!important;
       }


.select-list1 {
    -webkit-appearance: none;
    border-radius: 3px;
    background-image: url("{!$Resource.DownArrow}");
    background-repeat: no-repeat;
    background-position: 93% 10px;
    -webkit-appearance: none;
    -moz-appearance: none;
    text-indent: 0.01px;
    text-overflow: "";
    appearence: none;
  
}

select::-ms-expand {
    display: none;
}

.active a {
    background-color: #00AE58 !important;
    color: white !important;
}

.pagination li a {
    color: #00AE58;
}
.messageCell   { 
        background-color: #ffc !important;
     } 
     .container{ 
         margin-top: 10px; 
     } 
    .message {  
      background-color: #ffc !important;  
      border-style: solid !important; 
      border-width: 1px !important; 
      color: #000 !important; 
      padding: 6px 8px 6px 6px !important; 
      margin: 4px 20px !important; 
      }  
      .warningM3 { 
          border-color: #f90 !important;  
     }
     .ui-autocomplete { 
        height: 200px; 
        overflow-y: scroll; 
        overflow-x: hidden;
        }
        .font_style{font-family:'Open Sans','Arial',sans-bold;
            font-size:15px;line-height:1.428571429;color:#000000;}
  
  .font_heading{font-family:'Open Sans','Arial',sans-bold;
            font-size:15px;line-height:1.428571429;color:#000000;font-weight:bold;}
</style>

    <apex:outputPanel styleClass="isSiteDownPanel" html-isSiteDown="{!$Setup.Site_Maintenance_Setting__c.Girl_Registration_Maintenance__c}" />
    <script>
        var cachebuster = Math.round(new Date().getTime() / 1000);
        $("body").hide();
        if ( $(".isSiteDownPanel").attr("isSiteDown") == "true") { 
              var baseUrl = "{!$Site.CurrentSiteUrl}";
              window.location.href = baseUrl + 'InMaintenance';
        }
        $("body").show();
    </script>
    
    <apex:form >
        <c:Girl_RegistrationHeaderComponent />
        <apex:outputPanel styleClass="form-horizontal" id="searchPanel">

            <div class="container">
             <apex:actionStatus id="myStatus2" startText="Processing..."  stopText="" startStyleClass="statusStyle" >
                     <apex:facet name="start">
                   <div class="waitingSearchDiv" id="el_loading" style="background-color: #222;
                   height:230%; opacity:.75;width:100%; z-index:1041; position: absolute;"> 
                    <div class="waitingHolder" style="position: fixed;top: 50%;left: 50%;">
                    <img class="waitingImage" src="/img/loading.gif" title="Please Wait..." />
                    <span class="waitingDescription">Loading...</span>
                    </div>
                </div> </apex:facet>
                 </apex:actionStatus>
            <apex:pageMessages id="newPageMessage" />
                <div class="row">
                    <!-- <div class="col-sm-12" style="margin:20px 0;">
                            <div class="col-sm-2">&nbsp;</div>
                            <div class="col-sm-8">
                               <apex:outputPanel layout="block" >
                                 <br/>
                                  <apex:outputLabel value="Ways to participate" style="font-size: 22px;"/> -->
                                  <lable style="margin-left:4px;font-size:25px;font-weight:bold;">Ways to participate</lable><br/><br/>
                                  
                                   <apex:outputLabel value="What are you interested in? At Girl Scouts the possibilities to have fun and do big things are endless. Search opportunities for fun by age and location, or enter a specific troop or group number below." style="text-align: left; margin-left: 10px;"/>
                                    <apex:outputLabel value="Have a question or need more information? Simply click on the title of the opportunity you’d like to know more about." style="text-align: left; margin-left: 10px;"/>
                                    <apex:outputLabel value="If you’re not certain what opportunity you’d like to choose, select “unsure,” and let us know if you have questions. A staff member will reach out to you soon. " style="text-align: left; margin-left: 10px;"/><br/>
                                                   
                                  <!-- </apex:outputPanel>
                                </div>
                                <div class="col-sm-2 ">&nbsp;</div>
                         </div> -->
                         <!-- content end  -->
                         
                     </div>  
                  

                <div class="row">
                    <div class="col-sm-2"></div>
                    <div class="col-sm-5">
                      <div class="row"> 
                             <div class="col-sm-4">&nbsp;</div>
                            <div class="col-sm-6" ><apex:outputLabel value="Pick a specific Troop/Group:" id="troopLabel" styleClass=" control-label" /></div>
                       
                            
                            <div class="col-sm-4" style="float:left; padding-left:30px;margin-top:1%;"><apex:outputLabel value="Troop/Group#" id="troopOrGroupLabel" styleClass="control-label" style="text-align:right;" /></div>
                            <div class="col-sm-6 celpadding">
                                <apex:inputText value="{!troopOrGroupName}" id="troopNameLabel" styleClass="form-control searchCampaignName" />
                           
                        </div>
                        </div>
                    </div>
                 
                   
                    <div class="col-sm-5">
                        <div class="row">
                            <div class="col-sm-12">
                                <div class="col-sm-5">&nbsp;</div>
                                <div class="col-sm-7"> <apex:outputLabel value="Find an opportunity near you:" id="oppLabel" styleClass=" control-label" /></div>
                            </div>

                            <div class="col-sm-12 celpadding" >
                                <div class="col-sm-5" style="float:left; padding-left:111px;margin-top:1px;"><apex:outputLabel value="Zip Code" id="zipCodeLabel" styleClass="control-label" /></div>
                                <div class="col-sm-6"><apex:inputText value="{!zipCode}" id="zipCodeTxt" styleClass="form-control myZip" /></div>
                              
                            </div>

                            <div class="col-sm-12 celpadding">
                                <div class="col-sm-5" style="float:left; padding-left:75px;">
                                    <apex:outputLabel value="Radius (miles)" id="RadiusLabel" styleClass="control-label" style="text-align:right;"/>
                                </div>

                                <div class="col-sm-6">
                                    <apex:selectList value="{!selectedRadius}" multiselect="false" size="1" styleClass="form-control select-list1">
                                        <apex:selectOptions value="{!RadiusInMiles}" />
                                    </apex:selectList>
                                </div>
                             
                            </div>
                            <div class="col-sm-12 celpadding">
                            
                             <div class="col-sm-5" style="float:left; padding-left:25px; margin-top:1px;"><apex:outputLabel value="Grade as of Fall 2014" styleClass="control-label" /></div>
                            <div class="col-sm-6">
                                <apex:selectList value="{!Grade}" multiselect="false" size="1" styleClass="form-control select-list">
                                    <apex:selectOptions value="{!Items}" />
                                </apex:selectList>
                            </div>

                        </div>
                    </div>
                     <div class="col-sm-1"></div>
                </div>
   </div>

                <div class="row">
                 
                     <div class="col-sm-12 butright" style="margin-bottom:20px;">
                   <!-- <div class="col-sm-3" style="margin-right:10px;">-->
                        <apex:commandButton action="{!searchTroopORGroupRoleByNameORZip}"
                            value="Search" id="roleNameSearchBtn"
                             styleClass="btn button_reg" style="width:150px!important;" onclick="highlight();" />
                      &nbsp; &nbsp;
                       <apex:commandButton action="{!clearSelections}"
                            value="Clear Selections" id="clearSelectionBtn"
                            styleClass="btn button_reg" style="width:150px!important;" rerender="searchPanel" />
<!--                         <apex:commandButton action="{!communityLogin}" -->
<!--                             value="Login" id="loginBtn" -->
<!--                             styleClass="btn btn-default" onclick="highlight();" /> -->
                    </div>
                    </div>

                  


                        <apex:outputPanel id="searchResultPanel0" layout="block"
                            rendered="{! parentCampaignWrapperList.size > 0 }" >
                           <br/> <apex:outputLabel value="Your Selection" style="font-size: 18px;font-family: 'Open Sans','Arial',sans-bold !important;"/><br/>
                            <apex:pageBlock id="pgBlkSection0">
                           
                      
                                  
                                
                                <apex:dataTable value="{!parentcampaignWrapperList2}"
                                    var="campaignWrapper2" id="searchTable0"
                                    styleClass="table  table-bordered table-striped table2" style="margin-bottom: 0px;">
                                   
                           <apex:column >
                             
                            <apex:inputCheckbox styleClass="checkbox2" onclick="deleterecord('{!campaignWrapper2.campaignId}');"  />
                                        <apex:facet name="header">Remove From Selected</apex:facet>
                                    </apex:column>
                                   

                                    <apex:column styleClass="CampTitle">
                                        <!--                                                    <apex:outputLink value="{!campaignWrapper2.childCampaignName}" onclick="openPopup();return false;">{!campaignWrapper2.childCampaignName}</apex:outputLink> -->
                                        <!--                                                         <apex:outputLink value="" StyleClass="popupCls">{!campaignWrapper2.childCampaignName}</apex:outputLink>  -->
                                        <apex:inputHidden value="{!campaignWrapper2.campaignId}"
                                            id="theHiddenInput" />
                                        <apex:outputLink value=""
                                            onclick="showPopup(document.getElementById('{!$Component.theHiddenInput}').value)"
                                            StyleClass="popupCls">{!campaignWrapper2.childCampaignName}</apex:outputLink>
                                        <apex:facet name="header">Title</apex:facet>
                                    </apex:column>
                                    <!--                                             <apex:column value="{!campaignWrapper2.childCampaignName}" styleClass="CampTitle"> -->
                                    <!--                                                 <apex:facet name="header">Title</apex:facet> -->
                                    <!--                                             </apex:column> -->

                                    
                                    <apex:column value="{!campaignWrapper2.campaignGrade}">
                                        <apex:facet name="header">Grade</apex:facet>
                                    </apex:column>

                                    <apex:column value="{!campaignWrapper2.campaignMeetingLocation}">
                                        <apex:facet name="header">Location</apex:facet>
                                    </apex:column>

                                    <apex:column value="{!campaignWrapper2.campaignMeetingDay}">
                                        <apex:facet name="header">Day(s)</apex:facet>
                                    </apex:column>

                                     <apex:column >

                                         <apex:outputText styleClass="tz-target"
                                                           value="{0,date,MMM dd hh:mma yyyy }"
                                                           html-gmt-ts="{!campaignWrapper2.campaignMeetingStartDatetimeStd}">
                                              <apex:param value="{!campaignWrapper2.campaignMeetingStartDatetime}" />
                                         </apex:outputText>

                                         <apex:facet name="header">Start Date/Time</apex:facet>
                                    </apex:column>



                                  <apex:column value="{!campaignWrapper2.campaignOpeningsRemaining}">
                                        <apex:facet name="header">Openings Remaining</apex:facet>
                                    </apex:column>

                                    <apex:column value="{!campaignWrapper2.parentCampaignName}">
                                        <apex:facet name="header">Troop/Group#</apex:facet>
                                    </apex:column>

                                    <apex:column value="{!campaignWrapper2.campaignVolunteerReq}">
                                        <apex:facet name="header">Volunteers Needed</apex:facet>
                                    </apex:column>
                                     

                                </apex:dataTable>
                                    <div class="" style="margin-top: 8px;margin-left: 800px;">
                                  
                                    <apex:commandButton value="Next" id="nxtbtnsure"  action="{!addCampaignMember}"
                                        styleClass="button_reg btnnextsure" style="font-family: Open Sans,Arial!important;max-width: 120px;display: none;" rendered="{!isunsure==false }" />
                                    <apex:commandButton value="Next" id="nxtbtnunsure"  styleClass="button_reg unsureCls" style="font-family: Open Sans,Arial!important;max-width: 120px;display: none;" rendered="{!isunsure==true }"/>
                                        
                                        <apex:commandButton value="Processing..." disabled="true" 
                                        styleClass="button_reg btnnextsureprocessing" style="font-family: Open Sans,Arial!important;display: none;max-width: 120px;"/>
                                </div>
                            </apex:pageBlock>
                         
                            
                             
                </apex:outputPanel>
    
                        <apex:outputPanel id="searchResultPanel" layout="block"
                            rendered="{!parentCampaignWrapperList.size > 0}">
                            
                            <apex:outputLabel value="Search Results" style="font-size: 18px;font-family: 'Open Sans','Arial',sans-bold !important;"/><br/>
                                    <apex:dataTable value="{!parentCampaignWrapperList}"
                                    var="campaignWrapper" id="searchTable" styleClass="table  table-bordered table-striped">
                                    
                                    <apex:column >
                                      <apex:inputCheckbox value="{!campaignWrapper.isCampaignChecked}"
                                           onclick="onSelectcampaign('{!campaignWrapper.campaignId}');"  />
                                        <!-- Class-1 -->
                                    </apex:column>

                                    <apex:column value="{!campaignWrapper.campaignDistance}">
                                        <apex:facet name="header"><span class="font_heading">Distance (in miles)</span></apex:facet>
                                    </apex:column>

                                    <apex:column styleClass="CampTitle">
                                        <!--                                                    <apex:outputLink value="{!campaignWrapper.childCampaignName}" onclick="openPopup();return false;">{!campaignWrapper.childCampaignName}</apex:outputLink> -->
                                        <!--                                                         <apex:outputLink value="" StyleClass="popupCls">{!campaignWrapper.childCampaignName}</apex:outputLink>  -->
                                        <input type="hidden" id="hiddenFieldPopup" value="{!campaignWrapper.campaignId}"/>
                                        <apex:inputHidden value="{!campaignWrapper.campaignId}"
                                            id="theHiddenInput" />
                                        <apex:outputLink value=""
                                            onmousedown="showPopup(document.getElementById('{!$Component.theHiddenInput}').value); rightClickPopup(document.getElementById('{!$Component.theHiddenInput}').value)"
                                            StyleClass="popupCls">{!campaignWrapper.childCampaignName}</apex:outputLink>
                                        <apex:facet name="header">Title</apex:facet>
                                    </apex:column>
                                    <!--                                             <apex:column value="{!campaignWrapper.childCampaignName}" styleClass="CampTitle"> -->
                                    <!--                                                 <apex:facet name="header">Title</apex:facet> -->
                                    <!--                                             </apex:column> -->

                                    <apex:column value="{!campaignWrapper.campaignGrade}">
                                        <apex:facet name="header">Grade</apex:facet>
                                    </apex:column>

                                    <apex:column value="{!campaignWrapper.campaignMeetingLocation}">
                                        <apex:facet name="header">Location</apex:facet>
                                    </apex:column>

                                    <apex:column value="{!campaignWrapper.campaignMeetingDay}">
                                        <apex:facet name="header">Day(s)</apex:facet>
                                    </apex:column>

                                     <apex:column > 
                                      
                                         <apex:outputText styleClass="tz-target" 
                                                           value="{0,date,MMM dd hh:mma yyyy }" 
                                                           html-gmt-ts="{!campaignWrapper.campaignMeetingStartDatetimeStd}">
                                              <apex:param value="{!campaignWrapper.campaignMeetingStartDatetime}" /> 
                                         </apex:outputText> 
                                        
                                         <apex:facet name="header">Start Date/Time</apex:facet>
                                    </apex:column>
                                    
                                    

                                    <apex:column value="{!campaignWrapper.campaignOpeningsRemaining}">
                                        <apex:facet name="header">Openings Remaining</apex:facet>
                                    </apex:column>

                                    <apex:column value="{!campaignWrapper.parentCampaignName}">
                                        <apex:facet name="header">Troop/Group#</apex:facet>
                                    </apex:column>

                                    <apex:column value="{!campaignWrapper.campaignVolunteerReq}">
                                        <apex:facet name="header">Volunteers Needed</apex:facet>
                                    </apex:column>

                                </apex:dataTable>

                           
                            
                        <div class="row">

                                <div class="col-md-4">
                                    <apex:outputPanel id="pagerPanel" rendered="{!pagerFlag}">
                                        <ul class="pagination">
                                            <li class="previousLinkClick"><a
                                                href="javascript:void(0);">&laquo;</a></li>
                                            <apex:repeat value="{!pageNumberSet}" var="pageNumber"
                                                id="pager">
                                                <li class="target"><a href="javascript:void(0);">{!pageNumber}</a></li>
                                            </apex:repeat>
                                            <li class="nextLinkClick"><a href="javascript:void(0);">&raquo;</a></li>
                                        </ul>
                                    </apex:outputPanel>
                                </div>

                                <div class="col-md-4" style="margin-top: 22px;">
                                    <!--                                             <apex:panelGrid columns="2"  style="margin-top:22px; background-color:#fff!important;"> -->
                                    <div class="col-md-8">
                                        <apex:outputLabel value="Roles to display per Page"
                                            id="rolesPerPage" />
                                    </div>

                                    <div class="col-md-4">
                                        <apex:selectList value="{!selectedPageSize}"
                                            multiselect="false" size="1"
                                            styleClass="select-list1" style="width:50px!important;"
                                            onchange="onSelectPageSize();">
                                            <apex:selectOptions value="{!PageSizeOptions}" />
                                        </apex:selectList>
                                    </div>
                                    <!--                                             </apex:panelGrid> -->
                                </div>

                                <div class="col-md-4" style="margin-top: 22px;">
                                   
                                </div>
                            </div>    
                            
                    </apex:outputPanel>

                        

<!--                         </apex:outputPanel> -->

<!--                     </div> -->
<!--                 </div> -->
            </div>
             
        </apex:outputPanel>
      
        <apex:actionFunction action="{!deleteselectedrecord}" name="deleterecord" reRender="pgBlkSection0,searchTable0,nxtbtnsure,nxtbtnunsure" oncomplete="bindevents2();" status="myStatus2" >
   <apex:param name="tobedeleteselectedrecord" value="" assignTo="{!deleteselectedrecordid}"/>
     </apex:actionFunction>

         <apex:actionFunction name="onSelectcampaign"
            action="{!displayselectedcampaign}" status="myStatus2"
            rerender="pgBlkSection0,searchTable0,nxtbtnsure,nxtbtnunsure" oncomplete="bindevents2();">
            <apex:param name="tobeaddedselectedrecord" value="" assignTo="{!selectedcampaignidd}"/>
        </apex:actionFunction>
        <apex:actionFunction name="onSelectPageNumber"
            action="{!displayResultsOnPageNumberSelection}" status="myStatus"
            rerender="searchTable" oncomplete="bindevents();">
            <apex:param name="test1" assignTo="{!selectedPageNumber}" value="" />
        </apex:actionFunction>

        <apex:actionFunction name="onSelectPageSize"
            action="{!displayResultsOnPageNumberSelection}" status="myStatus"
            rerender="pagerPanel, searchTable "
            oncomplete="bindevents(); highLightComponent();">
            <apex:param name="test1" assignTo="{!selectedPageNumber}" value="" />
        </apex:actionFunction>

        <apex:actionFunction name="nextBtnCLick11" action="{!nextButtonClick}"
            status="myStatus" rerender="searchTable" oncomplete="bindevents();">
            <apex:param name="selectedPageNum1" assignTo="{!selectedPageNumber}"
                value="" />
        </apex:actionFunction>

        <apex:actionFunction name="prevBtnCLick11"
            action="{!previousButtonClick}" status="myStatus"
            rerender="searchTable" oncomplete="bindevents();">
            <apex:param name="selectedPageNum2" assignTo="{!selectedPageNumber}"
                value="" />
        </apex:actionFunction>

        <apex:actionFunction name="highlight" status="myStatus"
            rerender="searchTable"
            oncomplete="highLightComponent(); return false;">
        </apex:actionFunction>

        <!--  <apex:actionStatus startText="requesting..." id="myStatus">
                <apex:facet name="stop">{!selectedPageNumber}</apex:facet>
            </apex:actionStatus>
            -->

        <div class="modal fade" id="myModal" tabindex="-1" role="dialog"
            aria-labelledby="myModalLabel" aria-hidden="true" data-backdrop="static">
            <div class="modal-dialog">

                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal"
                            aria-hidden="true">&times;</button>
                    </div>

                    <div class="modal-body" style="height: 160px;">
                        <div class="form-group">
                            <div class="col-md-4">
                                <apex:outputLabel value="Why are you unsure of the Troop/Group you are selecting?" escape="false" />
                            </div>

                            <div class="col-md-4">
                                <apex:inputTextarea id="inputtext-1" value="{!whyAreYouUnsure}"
                                    styleClass="input-text-css form-control whyAreYouUnsureClass" style="resize: none" />
                            </div>
                            <div style="width:20px; float:left; color: red; font-size: 25px; margin-left: -10px;">*</div>
                        </div>
                    </div>

                    <div class="modal-footer">
                       <button type="button"  disabled="disabled" style="width:120px!important;display:none;" class="btn button_reg btnnextunsureprocessing">Processing...</button>
                        <button type="button" style="width:100px!important;" class="btn button_reg btnnextunsure" onclick="unsureBtnCLick();">Next</button>
<!--                         <apex:commandButton action="{!createCampaignMemberOnUnsureCheck}" -->
<!--                             value="Next" styleClass="btn btn-default " /> -->
                    </div>
                </div>
            </div>
        </div>



        <div class="modal fade" id="myPopup" tabindex="-1" role="dialog"
            aria-labelledby="myModalLabel" aria-hidden="true">
            <div class="modal-dialog">

                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal"
                            aria-hidden="true">&times;</button>
                    </div>

                    <div class="modal-body" style="height: 525px; width: 700px;">
                        <div id="content" style="width: 550px;">
                            <div class="colcontainer" id="main">
                                <div class="post">
                                    <div class="entry">
                                        <form action="#" class="formlist" method="post">
                                            <apex:outputPanel id="outPnl">
                                                <apex:repeat value="{!$ObjectType.Campaign.FieldSets.displayGirlTroopDetails}"
                                                    var="f">
                                                    <apex:outputText value="{!f.Label}" />
                                                    <span style="color: black">:</span>
                                                    <apex:outputField value="{!campaignPopup[f]}" />
                                                    <br />
                                                </apex:repeat>
                                            </apex:outputPanel>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

 
         <!-- ==================================================================================================== -->
          
             <div id="content1" style="width: 550px;display:none;">
                            <div class="colcontainer" id="main2">
                                <div class="post">
                                    <div class="entry">
                                        <form action="#" class="formlist" method="post">
                                            <apex:outputPanel id="outPn2">
                                                <apex:repeat value="{!$ObjectType.Campaign.FieldSets.displayGirlTroopDetails}"
                                                    var="f">
                                                    <apex:outputText value="{!f.Label}" />
                                                    <span style="color: black">:</span>
                                                    <apex:outputField value="{!campaignPopup[f]}" />
                                                    <br />
                                                </apex:repeat>
                                            </apex:outputPanel>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
          
         <!--  ==================================================================================================== -->     
        
        
        <div class="modal fade" id="confirmationPopup" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" data-backdrop="static">
                <div class="modal-dialog">
                    
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        </div>
      
                        <div class="modal-body">
                            <div class="form-group">
                                <div class="col-md-10">
                                    <apex:outputLabel value="Are you sure you want to register for more than one Troop/Group?" escape="false"  />
                                </div>
                            </div>
                        </div>
                        
                        <div class="modal-footer">
                            <button type="button" style="color: rgb(255, 255, 255) ! important; background: none repeat scroll 0% 0% rgb(0, 174, 88) ! important;" class="btn btn-default yes submitButton2">Yes</button>
                            <button type="button" style="color: rgb(255, 255, 255) ! important; background: none repeat scroll 0% 0% rgb(0, 174, 88) ! important;"  class="btn btn-default no submitButton2" >No</button>      
                        </div>
                    </div>
                </div>
            </div>
                

        <script type="text/javascript">
                function showPopup(val){
                    showPopup1(val);
                    //rightClickPopup(val);
                }
                
                
                function rightClickPopup(val)
                {
                  //alert('Test');
                  //alert(val);
                  //rightClickPopupRender(val);
                }
           </script>

        <apex:actionFunction name="rightClickPopupRender" action="{!showDetails}"
            reRender="outPn2" status="myStatus">
            <apex:param name="arg1" value="val" assignTo="{!campaignDetailsId}" />
        </apex:actionFunction>
        
        
        <apex:actionFunction name="showPopup1" action="{!showDetails}"
            reRender="outPnl" status="myStatus">
            <apex:param name="arg1" value="val" assignTo="{!campaignDetailsId}" />
        </apex:actionFunction>
        
        <apex:actionFunction name="whyAreYouUnsureFun" action="{!createCampaignMemberOnUnsureCheck}" reRender="abc" >
            <apex:param name="unsureParam1"  value="val" assignTo="{!whyAreYouUnsure}"/>
        </apex:actionFunction>
    
     <div class="modal fade" id="mynextbtnPopup" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" data-backdrop="static">
            <div class="modal-dialog_none" >
            <div style="top:50%; left:50%; position:absolute;">
            <img  class="waitingImage" src="/img/loading.gif" title="Please Wait..." /><span style="color:#ffffff;" class="waitingDescription">Loading...</span>
            </div>
            </div>
        </div>
    </apex:form>
<script>
        var newWin=null;    
        function openPopup(){  
            var url='/apex/TEZ_VFP_OpenPopupPage';  
            newWin=window.open(url, 'Popup','height=500,width=700,left=100,top=100,resizable=no,scrollbars=yes,toolbar=no,status=no');  
        }    
                                    
        function closePopup(){  
            if (null!=newWin){  
                newWin.close();  
            }    
        }  
    </script>

    <script>
     var ln=$('.table2 tr').length;
        if(ln==1){ 
        $(".btnnextsure").hide();
        $(".unsureCls").hide();
         $('.table2 tbody').append('<tr class="rem"><td></td><td></td><td></td><td></td><td></td><td style="color: red">No Troop/Group Selected</td><td></td><td></td><td></td></tr>');
        
        }else{
        $(".btnnextsure").show();
        $(".unsureCls").show();
        
        }
         var selectedYesForMore=false;
       function oncomplete(){
           $("li.target").first().addClass("active");
       }
        
        $(document).on('ready', function(){
            $("li.target").first().addClass("active");
        });

        function highLightComponent(){
            //alert('highLightComponent clicked');
            $("li.target").first().trigger('click');
        }
        
        $( ".target" ).click(function(event) {
            var text = $(this).text();
            $( ".target" ).removeClass("active");
            $(this).addClass("active");

            setPageNumberInController(text);
            
            return false;
        });
        
        function setPageNumberInController(test1){
            console.log('test1=>'+test1);
            onSelectPageNumber(test1);
        }
        
        $( ".nextLinkClick" ).click(function(event) {
            
            //nxtBtnClick();
            
            var currentSelectedValue = $("li.target.active").text();
          console.log('currentSelectedValue===>'+ currentSelectedValue);
          
          $("li.target.active").next().addClass("active")
          $("li.target.active").prev().removeClass("active")
          
          nextBtnCLick11(currentSelectedValue);
          
            return false;
        });
        
        $(".myZip").on('blur',function(){
            console.log('validating zip code....');
            var zip = $(this).val();
             if(zip.match(/^[0-9-+]+$/) === null || zip.length < 5){
                $(this).siblings("div").remove();
                $(this).after("<div> Invalid Zip Code.</div>");
                $(this).addClass(".alert alert-danger");
             } 
            else {
                $(this).siblings("div").remove();
                 $(this).removeClass(".alert alert-danger");
            }
        });
        /*function nxtBtnClick(){
        
          var currentSelectedValue = $("li.target.active").text();
          console.log('currentSelectedValue===>'+ currentSelectedValue);
          
          $("li.target.active").next().addClass("active")
          $("li.target.active").prev().removeClass("active")
          
          nextBtnCLick11(currentSelectedValue);
          
            return false;
        }*/
        
         $( ".previousLinkClick" ).click(function(event) {
            
            prevButtonClick();
            
            return false;
        });
        
        function prevButtonClick(){
        
          var currentSelectedValue = $("li.target.active").text();
          console.log('currentSelectedValue===>'+ currentSelectedValue);
          
          $("li.target.active").prev().addClass("active")
          $("li.target.active").next().removeClass("active")
          
          prevBtnCLick11(currentSelectedValue);
        }
        
         function bindevents2(){ 
              
            var ln2=$('.table2 tr').length;
            if(ln2==1){ 
            $(".btnnextsure").hide();
             $(".unsureCls").hide();
                $('.table2 tbody').append('<tr class="rem"><td></td><td></td><td></td><td></td><td></td><td style="color: red">No Troop/Group selected</td><td></td><td></td><td></td></tr>');
               
                }
            else{ 
            $(".btnnextsure").show();
            $(".unsureCls").show();
            $('.rem').closest('tr').remove();
            }
             $(':checkbox').each(function() { 
                this.checked = false;                      
            }); 
             $(".unsureCls").on('click',function(){
      $('#myModal').modal('toggle');
                    return false;
            });
       } 
        //For bindidng events when we select pagesize picklist
        function bindevents() {
            selectedYesForMore = false;
            $(".popupCls").on('click',function(){
                    //alert('====');
                    $('#myPopup').modal('toggle');
                    return false;
            });
            
            $( ".target" ).click(function(event) {
                var text = $(this).text();
                $( ".target" ).removeClass("active");
                $(this).addClass("active");

                setPageNumberInController(text);
                return false;
            });
        
            $( ".nextLinkClick" ).unbind('click').click(function(event) {
                
                //nxtBtnClick();
                var currentSelectedValue = $("li.target.active").text();
                console.log('currentSelectedValue===>'+ currentSelectedValue);
          
                $("li.target.active").next().addClass("active")
                $("li.target.active").prev().removeClass("active")
          
                nextBtnCLick11(currentSelectedValue);
                
                return false;
            });
            
            $( ".previousLinkClick" ).unbind('click').click(function(event) {
            
                prevButtonClick();
                return false;
            });
            
            $(document).on('ready', function(){
                $("li.target").first().addClass("active");
            });
            
            $(".chkBoxClass").on('change', function() {
                $(".chkBoxClass").not(this).prop('checked', false);  
            });
            
            $(".unsureCls").on('click',function(){
                    //alert('=====cls');
                   
                    $('#myModal').modal('toggle');
            });
            
        
            $(".close").on('click',function(){
                $(".unsureCls").prop('checked', false);
                $(".input-text-css").val("");
                
                $(".btnnextunsure").show();
                $(".btnnextunsureprocessing").hide();
            });
            
            $(".otherCls").on('click',function(event){
                
                console.log('on click event count=',$( ".otherCls:checked" ).length);
        
                if ( $( ".otherCls:checked" ).length == 2 && !selectedYesForMore){
                    console.log('has two checked');
                    $(event.target).addClass('selected-checkbox');
                    $('#confirmationPopup').modal('show');
                 return false;
                }
                
                return true;
            });
            
            $(".no").on('click',function(){
                $(".selected-checkbox").removeClass("selected-checkbox");
                $('.modal-backdrop').hide();
                $('#confirmationPopup').modal('hide');
                 return false;
            });
            
            $(".yes").on('click',function(){
                $(".selected-checkbox").prop("checked","checked");
                $(".selected-checkbox").removeClass( "selected-checkbox");
                $('.modal-backdrop').hide();
                $('#confirmationPopup').modal('hide');
                selectedYesForMore = true;
                return false;
            });
            
        }
        
      
            $(".unsureCls").on('click',function(){
                    //alert('====');
                    
                    $('#myModal').modal('toggle');
            });
            
        
            $(".close").on('click',function(){
                $(".unsureCls").prop('checked', false);
                $(".input-text-css").val("");
            });
        
           $(".searchCampaignName").autocomplete({ minLength: 1,overflow:scroll,
               
               source: function( request, response ) {
                              
                   console.log('request.term',request.term);
               
                   Visualforce.remoting.Manager.invokeAction('{!$RemoteAction.Girl_TroopGroupRoleSearchController.searchCampaingNames}',request.term, function(result, event){
                       response( result );
                   });
               },
               
               
               select: function( event, ui ) {
                   var terms =  [this.value];
                   terms.pop();
                   terms.push( ui.item.value );  
                   this.value = terms;
                   return false;
               },
               focus: function() {
                   return false;
               },
            },{escape: true});
            
            $(".popupCls").on('click',function(){
                   $('#myPopup').modal('toggle');
                    return false;
            }); 
            
          /*
          $('.popupCls').mousedown(function(event) {
         switch (event.which) {
        case 1:
           $('#myPopup').modal('toggle');
            return false;
            break;
        case 2:
            alert('Middle mouse button pressed');
            break;
        case 3:
             //alert('Right mouse button pressed');
               //rightClickPopup();
                var w = window.open("", "popupWindow", "width=600, height=400, scrollbars=yes");
                //var w = window.open();
                var html = $("#content1").html();
               //content1  myPopup
               $(w.document.body).html(html);
            break;
         default:
            alert('You have a strange mouse');
    }
 });
      */      
            $(".whyAreYouUnsureClass").on('blur',function() {
                console.log('validating whyAreYouUnsureClass....');
                var whyUnsure = $(this).val();
                if (whyUnsure === null || whyUnsure === '') {
                    $(this).siblings("div").remove();
                    $(this).after("<div> Reason is Required.</div>");
                    $(this).addClass(".alert alert-danger");
                } else {
                    $(this).siblings("div").remove();
                    $(this).removeClass(".alert alert-danger");
                }
            });
            
            
       //});

      
        
        //get the last checkbo
        function testchk(){
            $(".CampTitle").last().text();
            $(".table").find(".chkBoxClass").last();
            
            $(".CampTitle").last().text();
            var str = $(".CampTitle").last().text();
            
        }
        
        function uncheckAll() {
           
           if ($('.unsureCls').is(':checked')) {
               $('.otherCls').prop('checked', false);
               $('.irmCls').prop('checked', false);
               $('.otherParticipationCls').prop('checked', false);
            }
            
            if ($('.irmCls').is(':checked')) {
               $('.otherCls').prop('checked', false);
               $('.unsureCls').prop('checked', false);
            }
            
            if ($('.otherCls').is(':checked')) {
               $('.unsureCls').prop('checked', false);
               //$('.otherParticipationCls').prop('checked', false);
               $('.irmCls').prop('checked', false);
            }
            
        }
        $(".otherCls").on('click',function(event){
                
                console.log('on click event count=',$( ".otherCls:checked" ).length);
        
                if ( $( ".otherCls:checked" ).length == 2 && !selectedYesForMore){
                    console.log('has two checked');
                    $(event.target).addClass('selected-checkbox');
                    $('#confirmationPopup').modal('show');
                 return false;
                }
                
                return true;
            });
            
            $(".no").on('click',function(){
                $(".selected-checkbox").removeClass("selected-checkbox");
                $('.modal-backdrop').hide();
                $('#confirmationPopup').modal('hide');
                 return false;
            });
            
            $(".yes").on('click',function(){
                $(".selected-checkbox").prop("checked","checked");
                $(".selected-checkbox").removeClass( "selected-checkbox");
                $('.modal-backdrop').hide();
                $('#confirmationPopup').modal('hide');
                selectedYesForMore = true;
                return false;
            });
            
        function unsureBtnCLick() {

                //console.log('validating whyAreYouUnsureClass..67..'+document.getElementById('inputtext-1'));
                //alert($(".whyAreYouUnsureClass").val());
                var whyUnsure = $(".whyAreYouUnsureClass").val();
                console.log('whyUnsure==>'+whyUnsure);
                if (whyUnsure === null || whyUnsure === '') {
                    $(".whyAreYouUnsureClass").siblings("div").remove();
                    $(".whyAreYouUnsureClass").after("<div> Reason is Required.</div>");
                    $(".whyAreYouUnsureClass").addClass(".alert alert-danger");
                } else {
                    $(".whyAreYouUnsureClass").siblings("div").remove();
                    $(".whyAreYouUnsureClass").removeClass(".alert alert-danger");
                    whyAreYouUnsureFun(whyUnsure);
                    console.log('whyUnsure==>'+whyUnsure);
                }
            }
        
    </script>
    
    
    <script>
    var rc= rc || {};
    //converts timestamp in gmt to local time
    //selectorClass of element which has gmt-ts has attribute which ts in milli
    rc.formatDateTime = function (elementSelector,displayFormat){
    
        var gmtTZName = 'Etc/GMT';
        elementSelector = elementSelector || ".tz-target";
        displayFormat = displayFormat || 'MMMM DD hh:mmA YYYY z'; 
            
        $(elementSelector).each(function(index,tzElem){
    
            var timeStampInt =  parseInt($(tzElem).attr("gmt-ts"));
            
            if(timeStampInt==null || !timeStampInt)
                return true;
            
            var gmtTime= moment(timeStampInt).tz(gmtTZName);
            var locatTZName = jstz.determine().name(); 
            var locatTime = gmtTime.tz(locatTZName);
            var formatedTimeLocal = locatTime.format(displayFormat);
    
            if(formatedTimeLocal){
                $(tzElem).text(formatedTimeLocal);
            }
        });
    };
    
    $(document).ready(function(event){
        rc.formatDateTime();
    }); 
    </script>
    
    <br/>
    <c:Girl_Registration_Footer /> 
</apex:page>