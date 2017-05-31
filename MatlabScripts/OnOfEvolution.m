function OnOfEvolution()
%% Create a line plot with the evolution for Online approach

cd('../General');
X = load('evolution.txt');
B = reshape(X,[110,5]);

[a,b]=hist(B,unique(B));

figure % create new figure
hold on;
bar(a')
set(gca, 'xtick',1:5)

xlabel('time steps grouped in 100','fontweight','bold','fontsize',14)
ylabel('#times used each approach','fontweight','bold','fontsize',14)
title('types of evolution','fontweight','bold','fontsize',16)
legend('EVOLVED NOT SELECTED','SUCCESSFUL', 'REJECTED', 'Location','NorthWest')
% lg=legend('ONLINE','OFFLINE','Location','NorthWest')
% set(lg,'FontSize',14);
handle=gcf;
saveas(handle, ['evol', 'eps'],'epsc2')
hold off;
cd('../Functions');
end

