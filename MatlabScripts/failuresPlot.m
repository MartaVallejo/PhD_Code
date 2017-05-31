function failuresPlot()
% Create a stacked bar chart using the bar function

cd('../General');
fileID=fopen('Inconsistency.txt');
Z = textscan(fileID,'%d %d %d %d %d %s %d %d %d %s %d %s %s %s %d %d %d %d');
fclose(fileID);

figure;
y = [Z{3} Z{4}];
h = bar(y, 0.5, 'stack');
title('Test Failures','fontweight','bold','fontsize',16);
xlabel('Scenarios','fontweight','bold','fontsize',14);
ylabel('Times a failure occurs','fontweight','bold','fontsize',14);
scenarioNames={'1CBD - Const Prices','1CBD - Var Prices','3CBD - Var Prices'};
legend('Urban', 'Budget','Location','NorthWest');
set(h(1),'FaceColor',[0.5,1,0.3])
set(h(2),'FaceColor',[0.5,0,0])
set(gca,'xticklabel',scenarioNames)
handle=gcf;
saveas(handle, ['sumFailures'],'epsc')
cd('../Functions');
end

