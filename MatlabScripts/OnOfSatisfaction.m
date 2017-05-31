function OnOfSatisfaction()
%% Create a line plot with the satisfaction for Online/Offline/Mix

cd('../General');
X = load('satisfaction.txt');
A = grpstats(X, [X(:,1)]); 
steps = [1:1:600];
A1=A(1,3:end);
A2=A(2,3:end);
figure % create new figure
hold on;
hh = plot(steps(1:10:end), A1(1:10:end), '*', steps(1:10:end), A2(2:10:end), 'o');
set (hh, 'LineWidth', 2)
h=plot(steps, A(1,3:end), '--', steps, A(2,3:end), '--');
set (h, 'LineWidth', 1)

xlabel('time steps','fontweight','bold','fontsize',14)
ylabel('satisfaction','fontweight','bold','fontsize',14)
title('Performance Comparative','fontweight','bold','fontsize',16)
lg=legend('ONLINE','OFFLINE','Location','NorthWest')
set(lg,'FontSize',14);
handle=gcf;
saveas(handle, 'satisfaction','epsc2')
hold off;
cd('../Functions');
end

