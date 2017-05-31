function satisfactionArea()
%% Create a plot with the area satisfaction from three scenarios for one 
% of the optimisation approaches

cd('../General');
X = load('satisfactionArea.txt');
steps = [1:1:603];
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% statarray = grpstats(ds,groupvar) returns a dataset array with the means 
% for the data groups in the dataset array ds determined by the values of 
% the grouping variable or variables specified in groupvar.
XX = X(X(:,2)==0,:);
A1 = grpstats(XX, [XX(:,1)]);

YY = X(X(:,2)==1,:);
A2 = grpstats(YY, [YY(:,1)]);

ZZ = X(X(:,2)==2,:);
A3 = grpstats(ZZ, [ZZ(:,1)]);

% Create a bar plot with the satisfaction of three scenario
% Scenario 6
S11 = trapz(steps, A1(1,:));
% Scenario 9
S21 = trapz(steps, A1(2,:));
% Scenario 10
S31 = trapz(steps, A1(3,:));

S12 = trapz(steps, A2(1,:));
% Scenario 9
S22 = trapz(steps, A2(2,:));
% Scenario 10
S32 = trapz(steps, A2(3,:));

S13 = trapz(steps, A3(1,:));
% Scenario 9
S23 = trapz(steps, A3(2,:));
% Scenario 10
S33 = trapz(steps, A3(3,:));

B= [S11 S12 S13; S21 S22 S23; S31 S32 S33];
scenarioNames={'1CBD - Const Prices','1CBD - Var Prices','3CBD - Var Prices'};
bar(B)
title('Area Comparative','fontweight','bold','fontsize',15)
ylabel('Satisfaction Area','fontweight','bold','fontsize',14)
xlabel('Scenarios','fontweight','bold','fontsize',14)
legend('RAN','EA','CLO')
set(gca,'xticklabel',scenarioNames)
handle=gcf;
%saveas(handle, ['areaSatisfaction', 'png'],'png')
saveas(handle, ['areaSatisfaction', 'eps'],'epsc2')
cd('../Functions');


end

