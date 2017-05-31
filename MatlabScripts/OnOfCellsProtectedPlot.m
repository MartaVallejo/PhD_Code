function OnOfCellsProtectedPlot()
%% Create a plot with the number of cells protected for Online/Offline

cd('../General');
H = load('green.txt');
G = grpstats(H, [H(:,1)]);
steps = [1:1:600];

G1=G(1,2:end);
G2=G(2,2:end);
%figure % create new figure
hold on;
hh=plot(steps(1:10:end), G1(1:10:end), '*', steps(1:10:end), G2(1:10:end), 'o');
set (hh, 'LineWidth', 2)
h=plot(steps, G(1,2:end), '--', steps, G(2,2:end), '--');
set (h, 'LineWidth', 1)

axis([0,600,0,170])

ylabel('#green areas','fontweight','bold','fontsize',14)
xlabel('time steps','fontweight','bold','fontsize',14)
title('Number of Cells Protected','fontweight','bold','fontsize',16)
lg=legend('ONLINE','OFFLINE','Location','NorthWest')
set(lg,'FontSize',14);
handle=gcf;
saveas(handle, 'greencells','epsc2')
hold off;
cd('../Functions');

end

