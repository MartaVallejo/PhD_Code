function satisfaction_protected_closeness()
%% Three subplots: satisfaction, #cellsProtected, Closeness

cd('../General');
X = load('satisfaction.txt');
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Max element of each GA Satisfaction row
XX = max(X(X(:,1)==1,3:end)');
[A,ind] = max(XX); % Index of the max element to plot this run
SGA = X(ind,3:end); % row to print

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% statarray = grpstats(ds,groupvar) returns a dataset array with the means 
% for the data groups in the dataset array ds determined by the values of 
% the grouping variable or variables specified in groupvar.
A = grpstats(X, [X(:,1)]);  
H = load('green.txt');
G = grpstats(H, [H(:,1)]);
H = load('closeness.txt');
C = grpstats(H, [H(:,1)]);
steps = [1:1:600];

% Create a line plot with the satisfaction
A1=A(1,3:end);
A3=A(3,3:end);
figure % create new figure
hold on;
subplot(2,2,[1 2]) % first subplot
h=plot(steps, A(1,3:end), '--', steps, SGA, '--', steps, A(3,3:end), '--');
set (h, 'LineWidth', 1)
hh = plot(steps(1:10:end), A1(1:10:end), '*', steps(1:10:end), SGA(1:10:end), 'o', steps(1:10:end), A3(3:10:end), '+');
set (hh, 'LineWidth', 2)
ylabel('satisfaction','fontweight','bold','fontsize',14)
title('Satisfaction Comparative','fontweight','bold','fontsize',16)
legend('RAN','EA','CLO','Location','NorthWest')
hold off;

% Create a plot with the number of cells protected
G1=G(1,2:end);
G2=G(2,2:end);
G3=G(3,2:end);
hold on;
subplot(2,2,3) % second subplot
h=plot(steps, G(1,2:end), '--', steps, G(2,2:end), '--',steps, G(3,2:end), '--');
set (h, 'LineWidth', 1)
hh=plot(steps(1:10:end), G1(1:10:end), '*', steps(1:10:end), G2(1:10:end), 'o', steps(1:10:end), G3(3:10:end), '+');
set (hh, 'LineWidth', 2)
xlabel('time steps','fontweight','bold','fontsize',14)
ylabel('green cells','fontweight','bold','fontsize',14)
title('Number of Green Cells','fontweight','bold','fontsize',16)
hold off;

% Create a plot with the closeness to CBD
C1=C(1,2:end);
C2=C(2,2:end);
C3=C(3,2:end);
hold on;
subplot(2,2,4) % second subplot
h=plot(steps, C(1,2:end), '--', steps, C(2,2:end), '--',steps, C(3,2:end), '--');
set (h, 'LineWidth', 1)
hh=plot(steps(1:10:end), C1(1:10:end), '*', steps(1:10:end), C2(1:10:end), 'o', steps(1:10:end), C3(3:10:end), '+');
set (hh, 'LineWidth', 2)

xlabel('time steps','fontweight','bold','fontsize',14)
ylabel('distance','fontweight','bold','fontsize',14)
title('Closeness to CBD','fontweight','bold','fontsize',16)
handle=gcf;
%saveas(handle, ['closeness', 'png'],'png')
saveas(handle, ['closeness', 'eps'],'epsc2')
hold off;
cd('../Functions');

end

