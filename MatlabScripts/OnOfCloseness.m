function OnOfCloseness()
%% Create a plot with the closeness to CBD for Online/Offline/Mix

cd('../General');
H = load('closeness.txt');
C = grpstats(H, [H(:,1)]);
C1=C(1,2:end);
C2=C(2,2:end);
steps = [1:1:600];
figure % create new figure
hold on;
hh=plot(steps(1:10:end), C1(1:10:end), '*', steps(1:10:end), C2(1:10:end), 'o');
set (hh, 'LineWidth', 2)
h=plot(steps, C(1,2:end), '--', steps, C(2,2:end), '--');
set (h, 'LineWidth', 1)
axis([0,600,0,14])

xlabel('time steps','fontweight','bold','fontsize',14)
ylabel('avg distance to CBD','fontweight','bold','fontsize',14)
title('Closeness Factor','fontweight','bold','fontsize',16)
lg=legend('ONLINE','OFFLINE','Location','NorthWest')
set(lg,'FontSize',14);
handle=gcf;
saveas(handle, 'closeness','epsc2')
hold off;
cd('../Functions');

end

